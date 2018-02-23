/*
 *  ========================================================================
 *  uA Interpreter
 *  ========================================================================
 *  
 *  This file is part of ua Interpreter.
 *  
 *  ua Interpreter is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  ua Interpreter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with ua Interpreter.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2017, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package uk.ac.ljmu.fet.cs.comp.interpreter;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.EnumMap;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import uk.ac.ljmu.fet.cs.comp.interpreter.interfaces.UARunner;
import uk.ac.ljmu.fet.cs.comp.ub.UBMachine;
import uk.ac.ljmu.fet.cs.comp.ub.VInterpreter;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBEx;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBId;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBLab;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBReg;

public class Debugger implements UARunner {
	private VInterpreter itp;
	private int numLinesAllowed = -1;
	private String breakPointWaiter = "MyBreakPointObject";

	private InputVerifier checkNumbers = new InputVerifier() {
		@Override
		public boolean verify(JComponent input) {
			JTextField toVerify = (JTextField) input;
			try {
				int a = Integer.parseInt(toVerify.getText());
				if (a < 0)
					throw new NumberFormatException();
				toVerify.setBackground(Color.white);
				stContButton.setEnabled(true);
				nextButton.setEnabled(true);
				return true;
			} catch (NumberFormatException e) {
				stContButton.setEnabled(false);
				nextButton.setEnabled(false);
				toVerify.setBackground(Color.red);
				return false;
			}
		}
	};

	// Main window
	JFrame debugWindow = new JFrame(((char) 956) + "B Debug controls");

	// General info
	private JLabel currLineNo = new JLabel();
	private JLabel optionalLineInfo = new JLabel();
	private JLabel currLineContent = new JLabel();

	// Control buttons:
	private JButton stContButton = new JButton("Start");
	private JButton nextButton = new JButton("Next");

	private void startPressed() {
		if (stContButton.getText() == "Start") {
			stContButton.setText("Continue");
		}
		switchToRunning();
	}

	private void updateView() {
		currLineNo.setText("" + UBMachine.programCounter);
		UBEx ex = UBMachine.theProgram.get(UBMachine.programCounter);
		String liText = "";
		String clText = "N/A";
		if (ex != null) {
			clText = ex.toOriginalUB();
			if (ex.left instanceof UBId) {
				int ml = ((UBId) ex.left).getMemLoc();
				if (ml > 10000) {
					// the memory location is in either variable or constant space
					liText = ";\"" + ex.left.toOriginalUB() + "\" is at memory address " + ml;
				}
			}
		}
		optionalLineInfo.setText(liText);
		currLineContent.setText(clText);
		updateRegisters();
		noChangeAddressUpdate();
	}

	// Breakpoints:
	private JTextField lineNoBR = new JTextField(15);
	private JTextField labelBR = new JTextField(15);

	// Memory visualisation
	private JTextField address = new JTextField(6);
	private ArrayList<JLabel> addrDetails = new ArrayList<>();

	private void updateAddressDetails() {
		if (checkNumbers.verify(address)) {
			noChangeAddressUpdate();
		}
	}

	private void noChangeAddressUpdate() {
		int currAddress = Integer.parseInt(address.getText());
		int shift = 0;
		for (JLabel l : addrDetails) {
			try {
				int storedValue = UBMachine.getLocation(currAddress + shift++);
				l.setText("" + storedValue
						+ (storedValue < 128 && storedValue > 31 ? "   ['" + ((char) storedValue) + "']" : ""));
			} catch (Throwable e) {
				l.setText("N/A");
			}
		}
	}

	// Register visualisation
	private EnumMap<UBReg.RegType, JLabel> regLabelMap = new EnumMap<>(UBReg.RegType.class);

	private void updateRegisters() {
		for (UBReg.RegType currReg : UBReg.RegType.values()) {
			regLabelMap.get(currReg).setText("" + UBMachine.regValues.get(currReg));
		}
	}

	@Override
	public void run() {
		do {
			boolean doBreakPoint = false;
			if (numLinesAllowed == 0) {
				numLinesAllowed = -1;
				doBreakPoint = true;
			} else if (UBMachine.programCounter == Integer.parseInt(lineNoBR.getText().trim())) {
				doBreakPoint = true;
			} else {
				UBEx ex = UBMachine.theProgram.get(UBMachine.programCounter);
				if (ex instanceof UBLab) {
					String clabel = ((UBLab) ex).left.toOriginalUB();
					if (clabel.equals(labelBR.getText().trim())) {
						doBreakPoint = true;
					}
				}
			}
			if (doBreakPoint) {
				switchToSuspended();
			}
			numLinesAllowed--;
		} while (itp.interpret());
		debugWindow.dispose();
	}

	private void switchControlsTo(boolean state) {
		stContButton.setEnabled(state);
		nextButton.setEnabled(state);
		labelBR.setEnabled(state);
		lineNoBR.setEnabled(state);
		address.setEnabled(state);
	}

	private void switchToRunning() {
		switchControlsTo(false);
		synchronized (breakPointWaiter) {
			breakPointWaiter.notifyAll();
		}
	}

	private void switchToSuspended() {
		try {
			switchControlsTo(true);
			updateView();
			synchronized (breakPointWaiter) {
				breakPointWaiter.wait();
			}
		} catch (InterruptedException ex) {

		}
	}

	@Override
	public void initialize() {
		itp = new VInterpreter();

		String initialLNr = "" + UBMachine.programCounter;

		Container cp = debugWindow.getContentPane();
		cp.setLayout(new GridLayout(29, 1, 0, 0));
		// Main controls:
		JPanel topPanel = new JPanel();
		topPanel.add(stContButton);
		stContButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startPressed();
			}
		});
		topPanel.add(nextButton);
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				numLinesAllowed = 1;
				startPressed();
			}
		});
		cp.add(topPanel);

		// Basic info:
		cp.add(new JSeparator());
		JPanel linePanel = new JPanel();
		linePanel.add(new JLabel("Current line number:"));
		linePanel.add(currLineNo);
		cp.add(linePanel);
		cp.add(new JLabel("Current line:"));
		cp.add(optionalLineInfo);
		cp.add(currLineContent);

		// Breakpoint controls:
		cp.add(new JSeparator());
		cp.add(new JLabel("Breakpoint setup"));
		JPanel lineBRPanel = new JPanel();
		lineBRPanel.add(new JLabel("@ line:"));
		lineNoBR.setInputVerifier(checkNumbers);
		lineBRPanel.add(lineNoBR);
		lineNoBR.setText(initialLNr);
		cp.add(lineBRPanel);
		JPanel labelBRPanel = new JPanel();
		labelBRPanel.add(new JLabel("@ label:"));
		labelBRPanel.add(labelBR);
		labelBR.setText("entry");
		cp.add(labelBRPanel);

		// Register info:
		cp.add(new JSeparator());
		cp.add(new JLabel("Register inspector:"));
		for (UBReg.RegType currReg : UBReg.RegType.values()) {
			JPanel regPanel = new JPanel();
			regPanel.add(new JLabel(currReg + "="));
			JLabel regData = new JLabel();
			regPanel.add(regData);
			regLabelMap.put(currReg, regData);
			cp.add(regPanel);
		}

		// Memory info:
		cp.add(new JSeparator());
		cp.add(new JLabel("Memory inspector:"));
		JPanel memoryBase = new JPanel();
		memoryBase.add(new JLabel("address:"));
		address.setText("0");
		address.setInputVerifier(checkNumbers);
		address.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				updateAddressDetails();
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		address.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAddressDetails();
			}
		});
		memoryBase.add(address);
		cp.add(memoryBase);
		for (int i = 0; i < 10; i++) {
			JPanel adrPanel = new JPanel();
			adrPanel.add(new JLabel("+" + i + "="));
			JLabel adrData = new JLabel("0000000");
			adrPanel.add(adrData);
			addrDetails.add(adrData);
			cp.add(adrPanel);
		}

		updateView();
		debugWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		debugWindow.pack();
		debugWindow.setVisible(true);

	}
}
