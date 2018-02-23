/*
 *  ========================================================================
 *  uB Interpreter
 *  ========================================================================
 *  
 *  This file is part of uB Interpreter.
 *  
 *  uB Interpreter is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  uB Interpreter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with uB Interpreter.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2018, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package uk.ac.ljmu.fet.cs.comp.ub;

import java.io.FileReader;

import uk.ac.ljmu.fet.cs.comp.ub.generated.LexuB;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBEx;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBId;

public class ParseUB {
	public static void load(String file) throws Exception {
		LexuB lexer = new LexuB(new FileReader(file));
		SyntaxCheck sc = new SyntaxCheck();
		UBEx e = null;
		// Pass 1, lexing, syntax and identifier detection
		while ((e = lexer.yylex()) != null) {
			e.accept(sc);
			UBMachine.theProgram.put(e.myloc, e);
		}
		ReferenceCheck rc = new ReferenceCheck();
		// Pass 2, identifier references
		for (UBEx ex : UBMachine.theProgram.values()) {
			ex.accept(rc);
		}
		// Pass 3, final touches:
		// Setting up the limits of program execution
		e = SymbolTable.globalTable.get("start");
		if (e == null) {
			throw new Error("There is no place to start the program");
		}
		UBMachine.programCounter = ((UBId) e.left).getMemLoc();
		e = SymbolTable.globalTable.get("stop");
		if (e == null) {
			throw new Error("No label for the program's final line");
		}
		UBMachine.finalProgramAddress = ((UBId) e.left).getMemLoc();
	}
}
