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
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Register;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.STOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringValue;

public class InputResolver implements Visitor {
	private int resolvedValue;

	public int getResolvedValue() {
		return resolvedValue;
	}

	@Override
	public void visit(ADOperation e) {
		throwError(e);
	}

	@Override
	public void visit(CodeLabel e) {
		throwError(e);
	}

	@Override
	public void visit(DVOperation e) {
		throwError(e);
	}

	@Override
	public void visit(Identifier e) {
		resolvedValue = e.getMemLoc();
	}

	@Override
	public void visit(IntNumber e) {
		resolvedValue = e.containedValue;
	}

	@Override
	public void visit(JMOperation e) {
		throwError(e);
	}

	@Override
	public void visit(JZOperation e) {
		throwError(e);
	}

	@Override
	public void visit(LDOperation e) {
		throwError(e);
	}

	@Override
	public void visit(MLOperation e) {
		throwError(e);
	}

	@Override
	public void visit(MVOperation e) {
		throwError(e);
	}

	@Override
	public void visit(NumberConstant e) {
		throwError(e);
	}

	@Override
	public void visit(Register e) {
		resolvedValue = UAMachine.regValues[e.containedValue.ordinal()];
	}

	@Override
	public void visit(STOperation e) {
		throwError(e);
	}

	@Override
	public void visit(StringConstant e) {
		throwError(e);
	}

	private void throwError(Expression e) {
		throw new Error("Unexpected statement at line " + e.myloc);
	}

	@Override
	public void visit(StringValue e) {
		throw new Error("Unexpected string value as input spec at line " + e.myloc);
	}

}
