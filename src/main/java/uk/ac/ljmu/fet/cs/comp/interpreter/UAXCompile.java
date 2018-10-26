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

import java.io.BufferedWriter;
import java.io.FileWriter;

import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Expression;

public class UAXCompile {
	public static void main(String[] args) throws Exception {
		System.out.println("Parsing the file: " + args[0]);
		try {
			ParseUA.load(args[0]);
		} catch (Throwable e) {
			System.err.println("Could not parse the file " + args[0]);
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Compiling the file to uA");
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[0] + ".ua"));
		bw.write(";********************    Output    ********************\n");
		bw.write(";********************      of      ********************\n");
		bw.write(";******************** uAx compiler ********************\n");
		for (Expression ex : UAMachine.theProgram) {

			String uaLine = "\t; Code from uAx line: "+ex.myloc + "\n" + ex.toOriginalUA() + "\n\n";
			bw.write(uaLine);
		}
		bw.write(";********************    End of    ********************\n");
		bw.write(";********************  generated   ********************\n");
		bw.write(";********************     code     ********************\n");
		bw.close();
	}
}
