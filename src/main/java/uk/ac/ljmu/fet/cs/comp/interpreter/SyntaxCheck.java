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

import uk.ac.ljmu.fet.cs.comp.interpreter.interfaces.Visitor;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.ADOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.CodeLabel;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.DVOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Expression;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Identifier;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.IntNumber;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.JMOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.JZOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.LDOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.MLOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.MVOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.NumberConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Operation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Register;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.SBOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.STOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringValue;

public class SyntaxCheck implements Visitor {
	private int constantIndex = UAMachine.constants;

	@Override
	public void visit(Identifier e) {
		// Ignore
	}

	@Override
	public void visit(Register e) {
		// Ignore
	}

	// Registration of identifiers and saving their values to constant space
	@Override
	public void visit(NumberConstant e) {
		idDef(e);
	}

	@Override
	public void visit(StringConstant e) {
		idDef(e);
	}

	@Override
	public void visit(CodeLabel e) {
		idDef(e);
	}

	@Override
	public void visit(IntNumber e) {
		UAMachine.setConstant(constantIndex++, e.containedValue);
	}

	@Override
	public void visit(StringValue e) {
		for (int i = 0; i < e.containedValue.length(); i++) {
			UAMachine.setConstant(constantIndex++, e.containedValue.charAt(i));
		}
	}

	private void idDef(Expression e) {
		if (e.left instanceof Identifier) {
			Identifier i = (Identifier) e.left;
			if (SymbolTable.globalTable.containsKey(i.containedValue)) {
				throw new Error("Identifier name '" + i.containedValue + "' already in use current use at line "
						+ i.myloc + " previous use at line " + SymbolTable.globalTable.get(i.containedValue).myloc);
			}
			SymbolTable.globalTable.put(i.containedValue, e);
			if (e.right != null) {
				// Constant mapping
				i.setMemLoc(constantIndex);
				e.right.accept(this);
			} else {
				// Code labels
				i.setMemLoc(e.left.myloc);
			}
		} else {
			throw new Error("Non-identifier used in place of an id " + e.myloc);
		}
	}

	@Override
	public void visit(ADOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(SBOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(DVOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(JMOperation e) {
		visitOp(e,false);
	}

	@Override
	public void visit(JZOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(LDOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(MLOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(MVOperation e) {
		visitOp(e,true);
	}

	@Override
	public void visit(STOperation e) {
		visitOp(e,true);
	}

	private void visitOp(Operation e, boolean doRightCheck) {
		boolean invalidLeftExpr = false;
		switch (e.kind) {
		case R:
			invalidLeftExpr = !(e.left instanceof Register);
			break;
		case C:
			invalidLeftExpr = !((e.left instanceof Identifier) || (e.left instanceof IntNumber));
		}
		if (invalidLeftExpr) {
			throw new Error("Input parameter mismatch at line " + e.myloc);
		}
		if(doRightCheck) {
			if(e.right==null) {
				throw new Error("Output parameter missing at line " + e.myloc);
			}
			if(!(e.right instanceof Register)) {
				throw new Error("Output parameter is not a register at line " + e.myloc);
			}
		} else {
			if(e.right!=null) {
				throw new Error("Unexpected output parameter at line " + e.myloc);
			}
		}
	}
}
