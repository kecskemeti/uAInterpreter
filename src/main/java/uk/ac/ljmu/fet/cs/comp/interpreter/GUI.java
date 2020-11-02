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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import uk.ac.ljmu.fet.cs.comp.interpreter.interfaces.UARunner;

public class GUI {
	private static char mu = 956;
	private static JLabel[][] screen = new JLabel[UAMachine.screenHeight][UAMachine.screenWidth];
	private static JFrame mainWindow = new JFrame("Visualiser for the " + mu + "A interpreter");
	private static int pc = 0;
	public static Thread mainThread;

	private static void updateUAVisualiser() {
		for (int i = 0; i < UAMachine.screenHeight; i++) {
			for (int j = 0; j < UAMachine.screenWidth; j++) {
				screen[i][j].setText(Character.toString((char) UAMachine.getLocation(i * 80 + j)));
			}
		}
		mainWindow.repaint();
	}

	public static void errorAndExit(String msg) {
		System.err.println("Fatal error at line " + (pc + 1));
		System.err.println(msg);
		System.exit(1);
	}

	public static void main(String[] args) throws Exception {
		try {
			ParseUA.load(args[0]);
		} catch (Throwable e) {
			System.err.println("Could not parse the file " + args[0]);
			System.err.println(e.getMessage());
			System.exit(1);
		}
		// Parsing complete. Here comes the GUI

		mainThread = Thread.currentThread();
		Container cp = mainWindow.getContentPane();
		cp.setLayout(new GridLayout(UAMachine.screenHeight, UAMachine.screenWidth, 0, 0));
		for (int i = 0; i < UAMachine.screenHeight; i++) {
			for (int j = 0; j < UAMachine.screenWidth; j++) {
				screen[i][j] = new JLabel(" ");
				screen[i][j].setFont(Font.getFont(Font.MONOSPACED));
				screen[i][j].setPreferredSize(new Dimension(13, 13));
				cp.add(screen[i][j]);
			}
		}
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.pack();
		mainWindow.setVisible(true);
		mainWindow.setResizable(false);
		mainWindow.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// ignore
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// ignore
			}

			@Override
			public void keyPressed(KeyEvent e) {
				e.consume();
				UAMachine.setKeyboard(e.getKeyCode());
			}
		});
		new Thread() {
			@Override
			public void run() {
				do {
					try {
						// 60Hz refresh rate for the screen:
						sleep(1000 / 60);
					} catch (InterruptedException iex) {
						// ignore
					}
					updateUAVisualiser();
				} while (mainThread.isAlive());
				mainWindow.setTitle(mainWindow.getTitle() + " - Terminated");
				updateUAVisualiser();
			}
		}.start();

		// GUI Ready now we can run the program

		// Running the parsed program
		UARunner runner = new RegularRunner();
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("DEBUG")) {
				runner = new Debugger();
			}
		}
		runner.initialize();
		runner.run();
	}

}
