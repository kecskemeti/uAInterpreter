package uk.ac.ljmu.fet.cs.comp.calccomp.visitors;

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

public class UnreachableCodeDetector implements CalcVisitor {
	FunctionDeclarationStatement currentScope = null;
	boolean hadFinalAssign = false;

	private void checkFinalAssign(Statement e) {
		FunctionDeclarationStatement currFunction = e.getMyFunction();
		if (currFunction != currentScope) {
			if (currentScope != null && !hadFinalAssign) {
				e.throwError("No return value assignment in function "+currentScope.target.myId+". This function never returns to its caller");
			}
			hadFinalAssign = false;
			currentScope = currFunction;
		} else {
			if (hadFinalAssign) {
				e.throwError("Return value already assigned before, thus we have an unreachable statement here");
			} 
		}
		if (e.target.isReturnValue()) {
			hadFinalAssign = true;
		}
	}

	@Override
	public void visit(AdditionStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(SubtractionStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(AssignStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(DivisionStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(FunctionCallStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(MultiplyStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(PrintStatement e) {
		checkFinalAssign(e);
	}

	@Override
	public void visit(FunctionDeclarationStatement e) {
		// Nothing to do
	}

	@Override
	public void visit(VariableRef e) {
		// Nothing to do
	}

	@Override
	public void visit(AlterScope e) {
		// Nothing to do
	}

	@Override
	public void visit(CalcIntNumber e) {
		// Nothing to do
	}

}
