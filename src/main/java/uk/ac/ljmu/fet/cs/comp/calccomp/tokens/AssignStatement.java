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

public class AssignStatement extends Statement {
	public AssignStatement(int loc, VariableRef target, CalcExpression left, CalcExpression right) {
		super(loc, target, left, right);
	}

	@Override
	public void accept(CalcVisitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return "(" + super.toString() + "[" + target + "=" + left + "])";
	}

}
