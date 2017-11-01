package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisited;

public abstract class CalcExpression implements CalcVisited {
	public final int myloc;

	public CalcExpression(int myloc) {
		this.myloc = myloc + 1;
	}
}
