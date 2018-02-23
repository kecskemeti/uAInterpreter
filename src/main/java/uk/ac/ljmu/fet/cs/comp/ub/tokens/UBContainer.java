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
package uk.ac.ljmu.fet.cs.comp.ub.tokens;

public abstract class UBContainer<T> extends UBEx {
	public final T containedValue;
	public final boolean addressModifier;

	public UBContainer(int loc, T val, boolean addr) {
		super(loc, null, null);
		containedValue = val;
		addressModifier = addr;
	}

	@Override
	public String toOriginalUB() {
		return (addressModifier ? "@" : "") + containedValue.toString();
	}

	@Override
	public String toString() {
		return "[" + super.toString() + "=>" + containedValue + "]";
	}
}
