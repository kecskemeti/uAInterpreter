/*
 *  ========================================================================
 *  uB Interpreter
 *  ========================================================================
 *  
 *  This file is part of uB Interpreter.
 *  
 *  uB Interpreter is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  uB Interpreter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with uB Interpreter.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2018, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package uk.ac.ljmu.fet.cs.comp.ub;

import uk.ac.ljmu.fet.cs.comp.ub.interfaces.Visitor;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.ADDIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.CONDInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.DIVIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.JUMPInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.MOVEInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.MULIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBEx;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBId;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBInt;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBLab;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBNr;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBReg;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSc;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSt;

public class SyntaxCheck implements Visitor {
	private int constantIndex = UBMachine.constants;

	@Override
	public void visit(UBId e) {
		// Ignore
	}

	@Override
	public void visit(UBReg e) {
		// Ignore
	}

	// Registration of identifiers and saving their values to constant space
	@Override
	public void visit(UBNr e) {
		idDef(e);
	}

	@Override
	public void visit(UBSc e) {
		idDef(e);
	}

	@Override
	public void visit(UBLab e) {
		idDef(e);
	}

	@Override
	public void visit(UBInt e) {
		UBMachine.setConstant(constantIndex++, e.containedValue);
	}

	@Override
	public void visit(UBSt e) {
		for (int i = 0; i < e.containedValue.length(); i++) {
			UBMachine.setConstant(constantIndex++, e.containedValue.charAt(i));
		}
	}

	private void idDef(UBEx e) {
		if (e.left instanceof UBId) {
			UBId i = (UBId) e.left;
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
	public void visit(ADDIInst e) {
		arithmCheck(e);
	}

	@Override
	public void visit(DIVIInst e) {
		arithmCheck(e);
	}

	@Override
	public void visit(JUMPInst e) {
		visitOp(e, false);
	}

	@Override
	public void visit(CONDInst e) {
		visitOp(e, true);
		if (!(e.right instanceof UBReg)) {
			throw new Error("Output parameter is not a register at line " + e.myloc);
		}
	}

	@Override
	public void visit(MULIInst e) {
		arithmCheck(e);
	}

	@Override
	public void visit(MOVEInst e) {
		arithmCheck(e);
	}

	private void visitOp(UBInst e, boolean doRightCheck) {
		if(e.left.addressModifier && e.left instanceof UBId) {
			throw new Error("Cannot use identifiers as source address modifiers at line " + e.myloc);
		}
		if (doRightCheck) {
			if (e.right == null) {
				throw new Error("Output parameter missing at line " + e.myloc);
			}
			if (e.right.addressModifier && e.right instanceof UBId) {
				throw new Error("Cannot use identifiers as output address modifiers at line " + e.myloc);
			}
		} else {
			if (e.right != null) {
				throw new Error("Unexpected output parameter at line " + e.myloc);
			}
		}
	}

	private void arithmCheck(UBInst e) {
		visitOp(e, true);
		if (!e.right.addressModifier && e.right instanceof UBInt) {
			throw new Error("Cannot store output to an integer value " + e.myloc);
		}
	}
}
