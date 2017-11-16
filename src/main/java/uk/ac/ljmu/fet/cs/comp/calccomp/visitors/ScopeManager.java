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
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AdditionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AlterScope;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.AssignStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcExpression;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcIntNumber;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.DivisionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionCallStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionDeclarationStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.MultiplyStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.PrintStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.Statement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.SubtractionStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.VariableRef;

public class ScopeManager implements CalcVisitor {
	private FunctionDeclarationStatement currentFunction = CalcHelperStructures.globalFunction;
	private FunctionDeclarationStatement toBeUsedFunction = null;

	/**
	 * Ensures we don't use alterscope on the global level, tells the
	 * FunctionDeclarationStatement where are its boundaries in the source
	 */
	@Override
	public void visit(AlterScope e) {
	}

	/**
	 * Ensures we don't have declarations inside functions, registers the function
	 * in the list of functions
	 */
	@Override
	public void visit(FunctionDeclarationStatement e) {
	}

	/**
	 * Associates the expression with the current function
	 * 
	 * @param e
	 */
	private void addExpressionToScope(CalcExpression e) {
		if(toBeUsedFunction!=null && toBeUsedFunction!=currentFunction) {
			e.throwError("Unexpected operation in between scoping and function declaration");
		}
		e.addToFunction(currentFunction);
	}

	/**
	 * Ensures scope is referenced in all subexpressions of the statement
	 * 
	 * @param e
	 */
	private void addStatementToScope(Statement e) {
		addExpressionToScope(e);
		e.propagate(this);
	}

	@Override
	public void visit(AdditionStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(AssignStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(DivisionStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(FunctionCallStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(MultiplyStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(PrintStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(SubtractionStatement e) {
		addStatementToScope(e);
	}

	@Override
	public void visit(CalcIntNumber e) {
		addExpressionToScope(e);
	}

	@Override
	public void visit(VariableRef e) {
		addExpressionToScope(e);
	}
}
