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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Operation extends Expression<Expression, Register> {
	public static enum AttKind {
		C, R
	}

	public final AttKind kind;

	public Operation(int loc, Expression l, Register r, AttKind kind) {
		super(loc, l, r);
		this.kind = kind;
	}

	@Override
	public String toOriginalUA() {
		return getClass().getSimpleName().substring(0, 2) + kind + " " + left.toOriginalUA()
				+ (right == null ? "" : "," + right.toOriginalUA());
	}

	public static Operation opFactory(int loc, String opN, Expression l, Register r, AttKind kind)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		@SuppressWarnings("unchecked")
		Class<? extends Operation>[] ops = new Class[] { ADOperation.class, DVOperation.class, JMOperation.class,
				JZOperation.class, LDOperation.class, MLOperation.class, MVOperation.class, STOperation.class,
				SBOperation.class };
		for (Class<? extends Operation> op : ops) {
			if (op.getSimpleName().startsWith(opN)) {
				Constructor<? extends Operation> opConst = op.getConstructor(int.class, Expression.class,
						Register.class, AttKind.class);
				return opConst.newInstance(loc, l, r, kind);
			}
		}
		throw new Error("No such operation" + opN);
	}
}