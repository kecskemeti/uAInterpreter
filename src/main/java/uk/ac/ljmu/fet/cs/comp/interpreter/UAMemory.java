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

public class UAMemory {
	public static final int screenWidth = 80;
	public static final int screenHeight = 25;
	public static int totalMemory = 50000;
	public static int keyboard = screenWidth * screenHeight;
	public static int variables = 10000;
	public static int constants = 30000;

	private static int[] memory = new int[totalMemory];

	public static int getLocation(int loc) {
		if (totalMemory <= loc || 0 > loc) {
			throw new Error("Read reference to an invalid memory location (" + loc + ")");
		}
		return memory[loc];
	}

	public static void setLocation(int loc, int val) {
		if ((loc > keyboard && loc < variables) || loc >= constants) {
			throw new Error("Illegal memory write operation to location (" + loc + ")");
		}
		memory[loc] = val;
	}

	public static void setKeyboard(int val) {
		memory[keyboard] = val;
	}

	public static void setConstant(int loc, int val) {
		if (loc < constants) {
			throw new Error("Illegal constant memory setup operation to location (" + loc + ")");
		}
		memory[loc] = val;
	}

}
