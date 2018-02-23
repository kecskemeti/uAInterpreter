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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class UBInst extends UBEx<UBContainer, UBContainer> {

	public UBInst(int loc, UBContainer l, UBContainer r) {
		super(loc, l, r);
	}

	@Override
	public String toOriginalUB() {
		return getClass().getSimpleName().substring(0, 4) + " " + left.toOriginalUB()
				+ (right == null ? "" : "," + right.toOriginalUB());
	}

	public static UBInst instFactory(int loc, String opN, UBContainer l, UBContainer r) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		@SuppressWarnings("unchecked")
		Class<? extends UBInst>[] ops = new Class[] { ADDIInst.class, DIVIInst.class, JUMPInst.class, CONDInst.class,
				MULIInst.class, MOVEInst.class };
		for (Class<? extends UBInst> op : ops) {
			if (op.getSimpleName().startsWith(opN)) {
				Constructor<? extends UBInst> opConst = op.getConstructor(int.class, UBContainer.class, UBContainer.class);
				return opConst.newInstance(loc, l, r);
			}
		}
		throw new Error("No such operation" + opN);
	}
}