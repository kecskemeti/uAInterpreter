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

import java.util.EnumSet;

import uk.ac.ljmu.fet.cs.comp.ub.interfaces.Visitor;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBReg.RegType;

public class UBReg extends UBContainer<RegType> {
	public static enum RegType {
		A, B, C, D;
		public static RegType getByOrdial(int ordial) {
			for (RegType r : EnumSet.allOf(RegType.class)) {
				if (r.ordinal() == ordial) {
					return r;
				}
			}
			throw new Error("Invalid register spec");
		}

		public static RegType fromString(String v, int loc) {
			try {
				return RegType.valueOf(v);
			} catch (IllegalArgumentException e) {
				throw new Error("Invalid register identifier at line " + loc);
			}
		}
	}

	public UBReg(int loc, String type, boolean addr) {
		super(loc, RegType.fromString(type, loc), addr);
	}

	public UBReg(int loc, String type) {
		this(loc, type, false);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}