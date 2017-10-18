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
import java.util.HashMap;

import uk.ac.ljmu.fet.cs.comp.interpreter.generated.LexuA;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Expression;

public class UAProgram {
	public static HashMap<Integer, Expression> theProgram = new HashMap<>();

	public static void load(String file) throws Exception {
		LexuA lexer = new LexuA(new FileReader(file));
		SyntaxCheck sc = new SyntaxCheck();
		Expression e = null;
		// Pass 1:
		while ((e = lexer.yylex()) != null) {
			e.accept(sc);
			theProgram.put(e.myloc, e);
		}
		ReferenceCheck rc = new ReferenceCheck();
		// Pass 2:
		for (Expression ex : theProgram.values()) {
			ex.accept(rc);
		}
	}
}
