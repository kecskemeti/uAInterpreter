package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;

public class VariableRef extends CalcExpression {
	public final String myId;
	public int memLoc;

	public VariableRef(int loc, String id) {
		super(loc);
		myId = id;
	}
	
	@Override
	public void accept(CalcVisitor v) {
		v.visit(this);
	}
}
