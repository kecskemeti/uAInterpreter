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
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.SBOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.STOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringValue;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.VariableDefinition;

public class ReferenceCheck implements Visitor {

	@Override
	public void visit(Identifier i) {
		Expression ex = SymbolTable.globalTable.get(i.containedValue);
		if (ex == null) {
			throw new Error("Unknown identifier referenced at line " + i.myloc);
		}
		i.setMemLoc(((Identifier) ex.left).getMemLoc());
	}

	private void compositeDescent(Expression e) {
		if (e.left != null) {
			e.left.accept(this);
		}
		if (e.right != null) {
			e.right.accept(this);
		}
	}

	@Override
	public void visit(IntNumber e) {
		compositeDescent(e);
	}

	@Override
	public void visit(Register e) {
		compositeDescent(e);
	}

	@Override
	public void visit(StringValue e) {
		compositeDescent(e);
	}

	@Override
	public void visit(NumberConstant e) {
		compositeDescent(e);
	}

	@Override
	public void visit(StringConstant e) {
		compositeDescent(e);
	}

	@Override
	public void visit(CodeLabel e) {
		compositeDescent(e);
	}

	@Override
	public void visit(ADOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(SBOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(DVOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(JMOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(JZOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(LDOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(MLOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(MVOperation e) {
		compositeDescent(e);
	}

	@Override
	public void visit(STOperation e) {
		compositeDescent(e);
	}
	
	@Override
	public void visit(VariableDefinition e) {
		compositeDescent(e);
	}

}
