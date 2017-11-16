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
import java.util.ArrayList;

import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.CalcExpression;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionDeclarationStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.CalcSyntaxCheck;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.Dereferencing;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.ScopeManager;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.UACodeGenerator;
import uk.ac.ljmu.fet.cs.comp.calccomp.visitors.UnreachableCodeDetector;
import uk.ac.ljmu.fet.cs.comp.interpreter.generated.LexCalc;

public class CompileCalc {
	public static void main(String[] file) {
		try {
			LexCalc lexer = new LexCalc(new FileReader(file[0]));
			ArrayList<CalcExpression> expressions = new ArrayList<>();
			CalcSyntaxCheck sc = new CalcSyntaxCheck();
			// Pass 1, lexing and basic syntax checking
			for (CalcExpression ex = null; (ex = lexer.yylex()) != null;) {
				ex.accept(sc);
				expressions.add(ex);
			}

			// Pass 2, detecting scope for every expression
			ScopeManager sm = new ScopeManager();
			for (CalcExpression ex : expressions) {
				ex.accept(sm);
			}

			// Pass 3, dereferencing, ensures we have defined every variable in the
			// corresponding scope, as well as assigns an entry for the variables in the
			// frame of the function
			Dereferencing deref = new Dereferencing();
			for (CalcExpression ex : expressions) {
				ex.accept(deref);
			}

			// Pass 4: checking for unreachable code
			UnreachableCodeDetector ucd = new UnreachableCodeDetector();
			for (CalcExpression ex : expressions) {
				ex.accept(ucd);
			}

			// TODO: Should check: there is a generic case and only one

			// Pass 5, code generation
			UACodeGenerator uacg = new UACodeGenerator();
			for (FunctionDeclarationStatement f : CalcHelperStructures.allFunctions) {
				f.accept(uacg);
			}
			FileWriter fw = new FileWriter(file[0] + ".ua");
			fw.write(uacg.getGenerated());
			fw.close();
			System.out.println("Compilation successful.");
		} catch (Throwable e) {
			System.err.println("Compilation failed. Reason: " + e.getMessage());
		}
	}
}
