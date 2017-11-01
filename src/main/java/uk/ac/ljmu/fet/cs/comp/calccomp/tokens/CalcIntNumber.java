package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;

public class CalcIntNumber extends CalcExpression {
	public final int number;

	public CalcIntNumber(int loc, String val) {
		super(loc);
		number = Integer.parseInt(val);
	}
	
	@Override
	public void accept(CalcVisitor v) {
		v.visit(this);
	}

}
