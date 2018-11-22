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
 *  (C) Copyright 2018, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package uk.ac.ljmu.fet.cs.comp.interpreter.samplesasjava;

/*
 * This program encodes its command line input parameter to uA number constants
 * that are ready to be printed out once decoded by the HelloWorldBinary.ua sample. 
 */
public class EncodeForUABinDecoder {

	public static void main(String[] args) {
		// Merging all CLI parameters into a single string
		StringBuilder allArgs = new StringBuilder();
		for (String arg : args) {
			allArgs.append(arg).append(' ');
		}
		allArgs.deleteCharAt(allArgs.length() - 1);
		// Converting the single string to a byte array
		byte[] argsAsBytes = allArgs.toString().getBytes();
		// Turning the text into binary
		StringBuilder inBinary = new StringBuilder();
		int startLoc = 0;
		for (byte c : argsAsBytes) {
			for (int i = 0; i < 7; i++) {
				inBinary.insert(startLoc, ((c & 1) == 1) ? "1" : "0");
				c = (byte) (c >> 1);
			}
			startLoc += 7;
		}
		// 0 padding inBinary so it has a length divisible by 32
		for (int i = 32 - inBinary.length() % 32; i > 0; i--) {
			inBinary.append("0");
		}
		startLoc = 0;
		// converting inBinary to 32 bit integers
		while (startLoc < inBinary.length()) {
			StringBuilder ss = new StringBuilder(inBinary.substring(startLoc, startLoc + 32));
			// reversing so the decoder sees the highest bits first (this just speeds up the
			// conversion there)
			ss.reverse();
			if (ss.charAt(0) == '1') {
				// 1's Complement for negative numbers
				for (int i = 1; i < ss.length(); i++) {
					if (ss.charAt(i) == '1') {
						ss.setCharAt(i, '0');
					} else {
						ss.setCharAt(i, '1');
					}
				}
			}
			int currInt = Integer.parseInt((ss.charAt(0) == '1' ? "-" : "") + ss.substring(1), 2);
			// Generating the output to be replaced in HelloWorldBinary.ua
			System.out.println("CONNR hw" + (1 + startLoc / 32) + " " + currInt);
			startLoc += 32;
		}
	}
}
