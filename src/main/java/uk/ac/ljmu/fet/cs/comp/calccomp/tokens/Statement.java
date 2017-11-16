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
package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;

public abstract class Statement extends CalcExpression {

	public final VariableRef target;
	public final CalcExpression left, right;

	private final CalcExpression[] subexprs;

	public Statement(int loc, VariableRef target, CalcExpression left, CalcExpression right) {
		super(loc);
		this.target = target;
		this.left = left;
		this.right = right;
		subexprs = new CalcExpression[] { target, left, right };
	}

	@Override
	public void addToFunction(FunctionDeclarationStatement fn) {
		super.addToFunction(fn);
		myFunction.inScopeStatements.add(this);
	}

	public void propagate(CalcVisitor v) {
		for (CalcExpression se : subexprs) {
			if (se != null) {
				se.accept(v);
			}
		}
	}

	public static Statement statementFactory(int loc, String op, VariableRef target, CalcExpression left,
			CalcExpression right) {
		switch (op) {
		case "-":
			return new SubtractionStatement(loc, target, left, right);
		case "+":
			return new AdditionStatement(loc, target, left, right);
		case "/":
			return new DivisionStatement(loc, target, left, right);
		case "*":
			return new MultiplyStatement(loc, target, left, right);
		case "%":
			return new FunctionDeclarationStatement(loc, target, left, right);
		case "!":
			return new FunctionCallStatement(loc, target, left, right);
		case "":
			return new AssignStatement(loc, target, left, right);
		case "print":
			return new PrintStatement(loc, target, left, right);
		default:
			throw new Error("Unknown operation ('" + op + "') at line " + loc);
		}
	}

}
