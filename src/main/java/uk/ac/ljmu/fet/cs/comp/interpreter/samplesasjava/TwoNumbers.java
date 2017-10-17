/*
 *  ========================================================================
 *  uA Interpreter
 *  ========================================================================
 *  
 *  This file is sample source code for the ua Interpreter.
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
package uk.ac.ljmu.fet.cs.comp.interpreter.samplesasjava;

import java.io.IOException;

/*
 * This file implements the same functionality as we can see in SingleDigitCalculator.ua
 */
public class TwoNumbers {
	public static void main(String[] args) throws IOException {
		/*
		 * Note this only works in properly set up console
		 * 
		 * E.g., run the following command on a linux terminal before running this
		 * program:
		 * 
		 * stty -icanon min 1
		 * 
		 * The above command sets the console to single character mode.
		 */
		System.out.println("Please enter a 'large' single digit number");
		int firstChar = System.in.read();
		System.out.println();
		System.out.println("Please enter a 'smaller' single digit number");
		int nextChar = System.in.read();
		System.out.println();
		System.out.println("Subtracting your second from the first results in: " + (firstChar - nextChar));
	}
}