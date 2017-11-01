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
package uk.ac.ljmu.fet.cs.comp.calccomp;

import java.io.FileReader;
import java.io.FileWriter;

import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcExpression;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.Statement;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.CalcSyntaxCheck;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.UACodeGenerator;
import uk.ac.ljmu.fet.cs.comp.interpreter.generated.LexCalc;

public class CompileCalc {
	public static void main(String[] file) throws Exception {
		LexCalc lexer = new LexCalc(new FileReader(file[0]));
		CalcSyntaxCheck sc = new CalcSyntaxCheck();
		CalcExpression e = null;
		// Pass 1, lexing, syntax and identifier detection
		while ((e = lexer.yylex()) != null) {
			e.accept(sc);
			CalcHelperStructures.calcProgram.add((Statement) e);
		}
		// Pass 2, code generation
		UACodeGenerator uacg = new UACodeGenerator();
		for (Statement s : CalcHelperStructures.calcProgram) {
			s.accept(uacg);
		}
		FileWriter fw = new FileWriter(file[0] + ".ua");
		fw.write(uacg.getGenerated());
		fw.close();
	}
}
