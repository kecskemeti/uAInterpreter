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

import java.util.ArrayList;

import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Expression;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Register;

public class UAMachine {
	public static final int screenWidth = 80;
	public static final int screenHeight = 25;
	public static final int totalMemory = 50000;
	public static final int keyboard = screenWidth * screenHeight;
	public static final int variables = 10000;
	public static final int constants = 30000;
	public static int programCounter = -1;
	public static int finalProgramAddress = -1;
	public static ArrayList<Expression> theProgram = new ArrayList<>();
	public static int[] regValues = new int[Register.RegType.values().length];
	private static int[] memory = new int[totalMemory];

	public static int getLocation(int loc) {
		if (totalMemory <= loc || 0 > loc) {
			throw new Error("Read reference to an invalid memory location (" + loc + ")");
		}
		return memory[loc];
	}

	public static void setLocation(int loc, int val) {
		if ((loc >= variables && loc < constants) || loc < keyboard) {
			memory[loc] = val;
		} else {
			throw new Error("Illegal memory write operation to location (" + loc + ")");
		}
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
