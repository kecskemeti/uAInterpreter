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
