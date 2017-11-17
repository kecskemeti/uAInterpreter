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
package uk.ac.ljmu.fet.cs.comp.interpreter.interfaces;

import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.ADOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.CodeLabel;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.DVOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Identifier;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.IntNumber;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.JMOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.JZOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.LDOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.MLOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.MVOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.NumberConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Register;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.SBOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.STOperation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringValue;

public interface Visitor {
	void visit(ADOperation e);

	void visit(SBOperation e);

	void visit(CodeLabel e);

	void visit(DVOperation e);

	void visit(Identifier e);

	void visit(IntNumber e);

	void visit(JMOperation e);

	void visit(JZOperation e);

	void visit(LDOperation e);

	void visit(MLOperation e);

	void visit(MVOperation e);

	void visit(NumberConstant e);

	void visit(Register e);

	void visit(STOperation e);

	void visit(StringConstant e);

	void visit(StringValue e);
}
