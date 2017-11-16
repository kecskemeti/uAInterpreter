package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;

public class AlterScope extends CalcExpression {

	public AlterScope(int line) {
		super(line);
	}

	@Override
	public void accept(CalcVisitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return "AS(" + super.toString() + ")";
	}
}
