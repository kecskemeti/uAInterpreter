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

import uk.ac.ljmu.fet.cs.comp.calccomp.CalcHelperStructures;
import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcIntNumber;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.Statement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.VariableRef;
import uk.ac.ljmu.fet.cs.comp.interpreter.UAMachine;

public class CalcSyntaxCheck implements CalcVisitor {
	// The first 200 addresses are reserved for stack and other state info
	private int varAddr = UAMachine.variables + 200;

	@Override
	public void visit(CalcIntNumber e) {
		// do nothing
	}

	@Override
	public void visit(Statement e) {
		if (e.target == null) {
			throw new Error("No target specified for the statement in line" + e.myloc);
		}
		if (e.myKind != Statement.Kind.print) {
			if (e.left == null) {
				throw new Error("No left subexpression specified for the statement in line" + e.myloc);
			}
			// Assignment
			if (!CalcHelperStructures.globalSymbolTable.containsKey(e.target.myId)) {
				// First assignment
				CalcHelperStructures.globalSymbolTable.put(e.target.myId, e.target);
				// We allocate memory for this variable
				e.target.memLoc = varAddr++;
			}
			e.left.accept(this);
			if (e.right != null) {
				e.right.accept(this);
			} else {
				// No right side, this must not have an op (ie., this is the form of a=b or a=4
				if (e.myKind != null) {
					throw new Error("No right subexpression for the statement in line " + e.myloc);
				}
			}
		} else {
			// For print we want to make sure we print something defined
			e.target.accept(this);
		}
	}

	@Override
	public void visit(VariableRef e) {
		if (!CalcHelperStructures.globalSymbolTable.containsKey(e.myId)) {
			throw new Error("Using an unassigned variable ('" + e.myId + "') in line " + e.myloc);
		}
	}
}
