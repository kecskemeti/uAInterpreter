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

import java.util.Collection;

import uk.ac.ljmu.fet.cs.comp.calccomp.CalcHelperStructures;
import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AdditionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AlterScope;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AssignStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcIntNumber;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.DivisionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionCallStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionDeclarationStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.MultiplyStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.PrintStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.Statement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.SubtractionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.VariableRef;

public class Dereferencing implements CalcVisitor {

	private boolean declare(VariableRef v) {
		return false;
	}

	private void handleGenericStatement(Statement e) {
		declare(e.target);
		e.propagate(this);
	}

	@Override
	public void visit(AdditionStatement e) {
		handleGenericStatement(e);
	}

	@Override
	public void visit(AssignStatement e) {
		handleGenericStatement(e);
	}

	@Override
	public void visit(DivisionStatement e) {
		handleGenericStatement(e);
	}

	@Override
	public void visit(FunctionCallStatement e) {
		handleGenericStatement(e);
	}

	@Override
	public void visit(MultiplyStatement e) {
		handleGenericStatement(e);
	}

	@Override
	public void visit(SubtractionStatement e) {
		handleGenericStatement(e);
	}

	@Override
	public void visit(PrintStatement e) {
		e.target.accept(this);
	}

	@Override
	public void visit(FunctionDeclarationStatement e) {
	}

	@Override
	public void visit(AlterScope e) {
		// Nothing to do
	}

	@Override
	public void visit(CalcIntNumber e) {
		// Nothing to do
	}

	@Override
	public void visit(VariableRef e) {
	}

}
