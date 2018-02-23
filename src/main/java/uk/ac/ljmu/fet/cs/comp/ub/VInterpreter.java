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

import uk.ac.ljmu.fet.cs.comp.interpreter.ArtOp;
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

public class VInterpreter implements Visitor {
	private InputResolver ir = new InputResolver();

	public boolean interpret() {
		int pcBefore = UBMachine.programCounter;
		UBEx ex = UBMachine.theProgram.get(pcBefore);
		try {
			ex.accept(this);
			if (pcBefore == UBMachine.programCounter) {
				// There was no jump statement, we need to advance the pc ourselves
				UBMachine.programCounter++;
			}
		} catch (Throwable t) {
			throw new Error(t.getMessage() + " at line " + ex.myloc, t);
		}
		UBMachine.advancePCToNextNonEmpty();
		return UBMachine.finalProgramAddress != UBMachine.programCounter;
	}

	// Erroneous behaviour
	@Override
	public void visit(UBId e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(UBInt e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(UBReg e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(UBSt e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	// Operations

	@Override
	public void visit(UBLab e) {
		// Do nothing
	}

	@Override
	public void visit(UBNr e) {
		// Do nothing
	}

	@Override
	public void visit(UBSc e) {
		// Do nothing
	}

	// Arithmetics
	@Override
	public void visit(ADDIInst e) {
		doArithmetic(e, ArtOp.AD);
	}

	@Override
	public void visit(DIVIInst e) {
		doArithmetic(e, ArtOp.DV);
	}

	@Override
	public void visit(MULIInst e) {
		doArithmetic(e, ArtOp.ML);
	}

	// Goto/Jump constructs
	@Override
	public void visit(JUMPInst e) {
		uncJump(e);
	}

	@Override
	public void visit(CONDInst e) {
		e.right.accept(ir);
		if (ir.getResolvedValue() != 0) {
			uncJump(e);
		}
	}

	// Register operations
	@Override
	public void visit(MOVEInst e) {
		e.left.accept(ir);
		setVal(e, ir.getResolvedValue());
	}

	// Internal helpers
	private void doArithmetic(UBInst e, ArtOp op) {
		e.left.accept(ir);
		int leftVal = ir.getResolvedValue();
		e.right.accept(ir);
		setVal(e, op.realOP(ir.getResolvedValue(), leftVal));
	}

	private void setVal(UBInst e, int val) {
		if (e.right.addressModifier) {
			int addr = -1;
			if (e.right instanceof UBReg) {
				addr = UBMachine.regValues.get(((UBReg) e.right).containedValue);
			} else if (e.right instanceof UBInt) {
				addr = ((UBInt) e.right).containedValue;
			}
			UBMachine.setLocation(addr, val);
		} else {
			if(e.right instanceof UBReg) {
				UBMachine.regValues.put(((UBReg) e.right).containedValue, val);
			} else {
				throw new Error("Tried to write to a non-register..." + e.myloc);
			}
		}
	}

	private void uncJump(UBInst e) {
		e.left.accept(ir);
		UBMachine.programCounter = ir.getResolvedValue();
	}
}
