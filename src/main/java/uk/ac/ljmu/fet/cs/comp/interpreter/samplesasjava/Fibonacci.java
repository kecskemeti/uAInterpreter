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

public class Fibonacci {
	public static int fib(int n) {
		if(n==1) {
			return 1;
		}
		if(n==2) {
			return 1;
		}
		return fib(n-1)+fib(n-2);
	}
	public static void main(String[] args) {
		System.out.println(fib(9));
	}
}
