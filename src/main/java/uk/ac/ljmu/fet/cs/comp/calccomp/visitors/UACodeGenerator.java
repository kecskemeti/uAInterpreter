package uk.ac.ljmu.fet.cs.comp.calccomp.visitors;

import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcIntNumber;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.Statement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.VariableRef;

public class UACodeGenerator implements CalcVisitor {
	StringBuilder generated = new StringBuilder();

	@Override
	public void visit(CalcIntNumber e) {
	}

	@Override
	public void visit(Statement e) {
		
	}

	@Override
	public void visit(VariableRef e) {
	}
	
	public String getGenerated() {
		return generated.toString();
	}
}
