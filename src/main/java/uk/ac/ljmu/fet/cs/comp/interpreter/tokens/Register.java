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
package uk.ac.ljmu.fet.cs.comp.interpreter.tokens;

import java.util.EnumSet;

import uk.ac.ljmu.fet.cs.comp.interpreter.Interpret;
import uk.ac.ljmu.fet.cs.comp.interpreter.interfaces.Visitor;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Register.RegType;

public class Register extends ContainerExpression<RegType> {
	public static enum RegType {
		A, B, C, D;
		public static RegType getByOrdial(int ordial) {
			for (RegType r : EnumSet.allOf(RegType.class)) {
				if (r.ordinal() == ordial) {
					return r;
				}
			}
			Interpret.errorAndExit("Invalid register spec");
			return null;
		}

		public static RegType fromString(String v) {
			try {
				return RegType.valueOf(v);
			} catch (IllegalArgumentException e) {
				Interpret.errorAndExit("Invalid register identifier");
				return null;
			}

		}
	}
	public Register(int loc, String type) {
		super(loc,null,null, RegType.fromString(type));
	}
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}