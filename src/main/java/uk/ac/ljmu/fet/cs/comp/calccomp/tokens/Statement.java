package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;

public class Statement extends CalcExpression {
	public static enum Kind {
		print, add, subtract, multiply, divide;
		public static Kind opToKind(int loc, String op) {
			switch (op) {
			case "-":
				return subtract;
			case "+":
				return add;
			case "/":
				return divide;
			case "*":
				return multiply;
			default:
				throw new Error("Unknown operation ('" + op + "') at line " + loc);
			}
		}
	}

	public final Kind myKind;
	public final VariableRef target;
	public final CalcExpression left, right;

	public Statement(int loc, Kind k, VariableRef target, CalcExpression left, CalcExpression right) {
		super(loc);
		myKind = k;
		this.target = target;
		this.left = left;
		this.right = right;
	}

	@Override
	public void accept(CalcVisitor v) {
		v.visit(this);
	}

}
