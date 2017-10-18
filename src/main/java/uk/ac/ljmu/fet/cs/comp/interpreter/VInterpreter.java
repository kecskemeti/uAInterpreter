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
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.STOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringValue;

public class VInterpreter implements Visitor {
	private InputResolver ir = new InputResolver();

	public boolean interpret() {
		Expression ex=null;
		while ((ex = UAMachine.theProgram.get(UAMachine.programCounter++)) == null)
			;
		ex.accept(this);
		return UAMachine.finalProgramAddress != UAMachine.programCounter;
	}
	// Erroneous behaviour

	@Override
	public void visit(Identifier e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(IntNumber e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(Register e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(StringValue e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	// Operations

	@Override
	public void visit(CodeLabel e) {
		// Do nothing
	}

	@Override
	public void visit(NumberConstant e) {
		// Do nothing
	}

	@Override
	public void visit(StringConstant e) {
		// Do nothing
	}

	// Arithmetics
	@Override
	public void visit(ADOperation e) {
		doArithmetic(e, ArtOp.AD);
	}

	@Override
	public void visit(DVOperation e) {
		doArithmetic(e, ArtOp.DV);
	}

	@Override
	public void visit(MLOperation e) {
		doArithmetic(e, ArtOp.ML);
	}

	// Goto/Jump constructs
	@Override
	public void visit(JMOperation e) {
		uncJump(e);
	}

	@Override
	public void visit(JZOperation e) {
		e.right.accept(ir);
		if (ir.getResolvedValue() != 0) {
			uncJump(e);
		}
	}

	// Memory operations
	@Override
	public void visit(LDOperation e) {
		e.left.accept(ir);
		setReg(e, UAMachine.getLocation(ir.getResolvedValue()));
	}

	@Override
	public void visit(STOperation e) {
		e.left.accept(ir);
		int leftVal = ir.getResolvedValue();
		e.right.accept(ir);
		UAMachine.setLocation(leftVal, ir.getResolvedValue());
	}

	// Register operations
	@Override
	public void visit(MVOperation e) {
		e.left.accept(ir);
		setReg(e, ir.getResolvedValue());
	}

	// Internal helpers
	private void doArithmetic(Operation e, ArtOp op) {
		e.left.accept(ir);
		int leftVal = ir.getResolvedValue();
		e.right.accept(ir);
		setReg(e, op.realOP(ir.getResolvedValue(),leftVal));
	}

	private void setReg(Operation e, int val) {
		UAMachine.regValues.put(((Register) e.right).containedValue, val);
	}

	private void uncJump(Operation e) {
		e.left.accept(ir);
		UAMachine.programCounter = ir.getResolvedValue();
	}
}
