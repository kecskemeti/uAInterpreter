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
package uk.ac.ljmu.fet.cs.comp.interpreter;

import java.io.FileReader;

import uk.ac.ljmu.fet.cs.comp.interpreter.generated.LexuA;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Expression;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Identifier;

public class ParseUA {
	public static void load(String file) throws Exception {
		LexuA lexer = new LexuA(new FileReader(file));
		SyntaxCheck sc = new SyntaxCheck();
		Expression e = null;
		// Pass 1, lexing, syntax and identifier detection
		while ((e = lexer.yylex()) != null) {
			e.accept(sc);
			UAMachine.theProgram.put(e.myloc, e);
		}
		ReferenceCheck rc = new ReferenceCheck();
		// Pass 2, identifier references
		for (Expression ex : UAMachine.theProgram.values()) {
			ex.accept(rc);
		}
		// Pass 3, final touches:
		// Setting up the limits of program execution
		e = SymbolTable.globalTable.get("entry");
		if (e == null) {
			throw new Error("No program entry label is defined");
		}
		UAMachine.programCounter = ((Identifier) e.left).getMemLoc();
		e = SymbolTable.globalTable.get("exit");
		if (e == null) {
			throw new Error("No program termination label is defined");
		}
		UAMachine.finalProgramAddress = ((Identifier) e.left).getMemLoc();
	}
}
