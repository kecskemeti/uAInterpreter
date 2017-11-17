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
package uk.ac.ljmu.fet.cs.comp.calccomp.visitors;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.ljmu.fet.cs.comp.calccomp.CalcHelperStructures;
import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AdditionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AlterScope;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AssignStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcExpression;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcIntNumber;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.DivisionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionCallStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionDeclarationStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.MultiplyStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.PrintStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.Statement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.SubtractionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.VariableRef;

public class UACodeGenerator implements CalcVisitor {
	// Globally used helpers
	private static String endLabel = "exit:\nJMC exit\n";
	private StringBuilder generated = new StringBuilder();

	// Helpers for the print
	private static final String printNumMainFunction = "printNum";
	private static final String printNumRetLabel = "retPrintNum";
	private static final String printLocVar = "29999";
	private int printLabelCount = 0;

	// Helpers for the prelude of the generated uA code
	private boolean firstPrint = true;

	// Helpers for the variables and constants
	private boolean inLoad = true;
	private String targetReg;

	// Helpers for functions
	private static final String funcAltCaseLabel = "funcAltCase";
	private static final String endCallLabel = "endCall";
	private VariableRef currentfunctionCallTarget=null;
	private int casecounter = 0;
	private int callcounter = 0;

	@Override
	public void visit(CalcIntNumber e) {
		if (inLoad) {
			generated.append("MVC ");
			generated.append(e.number);
			generated.append(',');
			generated.append(targetReg);
			generated.append("\n");
		}
	}

	/**
	 * does !return! + LOAD + STORE depending on the reference and the state of the
	 * code generator
	 */
	@Override
	public void visit(VariableRef e) {
		// Adjust our pointer within the stack frame
		generated.append("ADC -");
		generated.append(e.memLoc);
		generated.append(",D\n");
		if (inLoad) {
			generated.append("LDR D,");
		} else {
			generated.append("STR D,");
		}
		generated.append(targetReg);
		generated.append("\n");
		// Move the stack pointer back
		generated.append("ADC ");
		generated.append(e.memLoc);
		generated.append(",D\n");
		if (e.isReturnValue()) {
			if (inLoad) {
				throw new RuntimeException("This is supposedly the final store then why are we at load?");
			}

			// The top of the stack is the return address
			generated.append("LDR D,A\n");
			// We forget about our current frame
			generated.append("ADC -");
			generated.append(e.getMyFunction().getFrameIndex());
			generated.append(",D\n");
			// Return where we need to go
			generated.append("JMR A\n");
		}
	}

	private void loadToReg(CalcExpression ce, String target) {
		if (ce != null) {
			inLoad = true;
			this.targetReg = target;
			ce.accept(this);
		}
	}

	private void storeResult(CalcExpression e) {
		inLoad = false;
		targetReg = "A";
		e.accept(this);
	}

	private void insertLabel(String label) {
		generated.append("\n");
		generated.append(label);
		generated.append(":\n");
	}

	private void genArithmetics(Statement e, String uAOp) {
		genericStatementActions(e);
		loadToReg(e.left, "A");
		if (uAOp != "") {
			loadToReg(e.right, "B");
			generated.append(uAOp);
			generated.append(" B,A\n");
		}
		storeResult(e.target);
	}

	private void genericStatementActions(Statement e) {
		// Every line has its own label (helpful to figure out where we are in the
		// original code, might be useful for a debugger
		insertLabel("line" + e.myloc);
	}

	@Override
	public void visit(AdditionStatement e) {
		genArithmetics(e, "ADR");
	}

	@Override
	public void visit(SubtractionStatement e) {
		genArithmetics(e, "MLC -1,B\nADR");
	}

	@Override
	public void visit(AssignStatement e) {
		genArithmetics(e, "");
	}

	@Override
	public void visit(DivisionStatement e) {
		genArithmetics(e, "DVR");
	}

	@Override
	public void visit(MultiplyStatement e) {
		genArithmetics(e, "MLR");
	}

	@Override
	public void visit(PrintStatement e) {
		genericStatementActions(e);
		if (firstPrint) { // Inserts the printNum function wherever we are and jumps over it
			firstPrint = false;
			generated.append("\n\n\n; BEGINNING OF Printnum internal function\n");
			generated.append("JMC afterPrintNum\n");
			insertLabel(printNumMainFunction);
			generated.append("LDR D,A\n");
			generated.append("ADC -1,D\n");
			generated.append("LDR D,B\n");
			generated.append("ADC -1,D\n");
			generated.append("ADC 9,B\n");
			insertLabel("hasMoreDigits");
			generated.append("MVR A,C\n");
			generated.append("DVC 10,C\n");
			generated.append("MLC -10,C\n");
			generated.append("ADR A,C\n");
			generated.append("ADC 48,C\n");
			generated.append("STR B,C\n");
			generated.append("ADC -1,B\n");
			generated.append("DVC 10,A\n");
			generated.append("JZC hasMoreDigits,A\n");
			generated.append("LDR D,A\n");
			generated.append("ADC -1,D\n");
			generated.append("JMR A\n");
			insertLabel("afterPrintNum");
			generated.append("; END OF Printnum internal function \n\n\n");
		}
		// We load the to be printed value to C
		loadToReg(e.target, "C");
		// The actual printing
		String currLabel = printNumRetLabel + printLabelCount++;
		// push return address:
		generated.append("MVC ");
		generated.append(currLabel);
		generated.append(",A\n");
		generated.append("ADC 1,D\n");
		generated.append("STR D,A\n");
		// calc current position
		generated.append("LDC ");
		generated.append(printLocVar);
		generated.append(",A\n");
		// Moving to the next line:
		generated.append("ADC 80,A\n");
		// Modulo 2000:
		generated.append("MVR A,B\n");
		generated.append("DVC 2000,B\n");
		generated.append("MLC -2000,B\n");
		generated.append("ADR B,A\n");
		// Remembering where we need to go next time
		generated.append("STC ");
		generated.append(printLocVar);
		generated.append(",A\n");
		// Storing the calculated position to the stack
		generated.append("ADC 1,D\n");
		generated.append("STR D,A\n");
		// Storing the to be printed value on the stack
		generated.append("ADC 1,D\n");
		generated.append("STR D,C\n");
		generated.append("JMC "); // Placing the return label
		generated.append(printNumMainFunction);
		generated.append("\n");
		insertLabel(currLabel);
	}

	@Override
	public void visit(FunctionCallStatement e) {
		genericStatementActions(e);
		String endLabel = endCallLabel + callcounter++;
		ArrayList<FunctionDeclarationStatement> myAlts = new ArrayList<>(
				CalcHelperStructures.alternatives.get(((VariableRef) e.left).myId));
		currentfunctionCallTarget=e.target;
		// The input parameter of the call is loaded to register A
		loadToReg(e.right, "A");
		if (myAlts.size() != 1) {
			// conditionals are needed to determine which subcase of the function we will
			// need to do, these must all depend on what is the actual value of the input
			Iterator<FunctionDeclarationStatement> it = myAlts.iterator();
			while (it.hasNext()) {
				FunctionDeclarationStatement fds = it.next();
				if (fds.left instanceof CalcIntNumber) {
					it.remove();
					String currLabel = funcAltCaseLabel + casecounter++;
					CalcIntNumber currCase = (CalcIntNumber) fds.left;
					// specific case, we remove it so the direct call could occur last
					// Then we generate a conditional for the call at hand.
					generated.append("MVR A,B\n");
					generated.append("ADC -");
					generated.append(currCase.number);
					generated.append(",B\n");
					// Jump to the next case
					generated.append("JZC ");
					generated.append(currLabel);
					generated.append(",B\n");
					// Call this subcase
					genUserFunctionCall(fds, endLabel);
					// Prepare the field for the next subcase:
					insertLabel(currLabel);
				}
			}
		}
		// Direct call
		genUserFunctionCall(myAlts.get(0), endLabel);
		// This is where we return once the call is done
		insertLabel(endLabel);
	}

	/**
	 * Assumes register A holds the input to the function
	 * 
	 * @param forFunc
	 * @param retLabel
	 */
	private void genUserFunctionCall(FunctionDeclarationStatement forFunc, String retLabel) {
		String intermediateLabel = endCallLabel + "IM" + callcounter++;
		// Adjust the stack for the function's stack frame
		generated.append("ADC ");
		generated.append(forFunc.getFrameIndex());
		generated.append(",D\n");
		// Stack looks like D-1= return value, D return loc
		generated.append("MVC ");
		generated.append(intermediateLabel);
		generated.append(",B\n");
		generated.append("STR D,B\n");
		// Let's put the input (from reg A) on the just created stackframe (if there is
		// any)
		if (forFunc.left instanceof VariableRef) {
			storeResult(forFunc.left);
		}
		// The frame is ready, we can jump to the function now.
		generated.append("JMC ");
		generated.append(forFunc.getCanonicalName().myId);
		generated.append("\n");
		insertLabel(intermediateLabel);
		// We just need to recall the return value from the stack
		// We load to A so we can store it with our store result function
		int returnValueShift=forFunc.getFrameIndex()-FunctionDeclarationStatement.returnValueIndicator;
		generated.append("ADC ");
		generated.append(returnValueShift);
		generated.append(",D\n");
		generated.append("LDR D,A\n");
		generated.append("ADC -");
		generated.append(returnValueShift);
		generated.append(",D\n");
		// We store it where we need it and the call is now really done
		storeResult(currentfunctionCallTarget);
		generated.append("JMC ");
		generated.append(retLabel);
	}

	@Override
	public void visit(FunctionDeclarationStatement e) {
		genericStatementActions(e);
		boolean isGlobal = e == CalcHelperStructures.globalFunction;
		if (isGlobal) {
			// This is our main function, we need to make sure it has its uA equivalent
			// entry symbol
			insertLabel("entry");
			// The top of the stack is 10000
			generated.append("MVC ");
			generated.append(9999 + CalcHelperStructures.globalFunction.getFrameIndex());
			generated.append(",D\n");
			// Store the initial cursor position (i.e., the top of the screen)
			generated.append("MVC -80,A\n");
			generated.append("STC ");
			generated.append(printLocVar);
			generated.append(",A\n");
		}
		insertLabel(e.getCanonicalName().myId);
		// Allows return to adjust the framesize back
		// Generate the function itself:
		for (Statement s : e.inScopeStatements) {
			s.accept(this);
		}
		if (isGlobal) {
			generated.append(endLabel);
		}
	}

	@Override
	public void visit(AlterScope e) {
		// Do nothing
	}

	public String getGenerated() {
		// Finalizing the code
		return generated.toString();
	}

}
