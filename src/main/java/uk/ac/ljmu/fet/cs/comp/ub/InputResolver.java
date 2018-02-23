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
package uk.ac.ljmu.fet.cs.comp.ub;

import uk.ac.ljmu.fet.cs.comp.ub.interfaces.Visitor;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.ADDIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.CONDInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.DIVIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.JUMPInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.MOVEInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.MULIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBContainer;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBEx;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBId;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBInt;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBLab;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBNr;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBReg;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSc;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSt;

public class InputResolver implements Visitor {
	private int resolvedValue;

	public int getResolvedValue() {
		return resolvedValue;
	}

	@Override
	public void visit(ADDIInst e) {
		throwError(e);
	}

	@Override
	public void visit(UBLab e) {
		throwError(e);
	}

	@Override
	public void visit(DIVIInst e) {
		throwError(e);
	}

	@Override
	public void visit(UBId e) {
		resolvedValue = e.getMemLoc();
	}

	@Override
	public void visit(UBInt e) {
		resolvedValue = e.containedValue;
		addressResolver(e);
	}

	@Override
	public void visit(JUMPInst e) {
		throwError(e);
	}

	@Override
	public void visit(CONDInst e) {
		throwError(e);
	}

	@Override
	public void visit(MULIInst e) {
		throwError(e);
	}

	@Override
	public void visit(MOVEInst e) {
		throwError(e);
	}

	@Override
	public void visit(UBNr e) {
		throwError(e);
	}

	@Override
	public void visit(UBReg e) {
		resolvedValue = UBMachine.regValues.get(e.containedValue);
		addressResolver(e);
	}

	@Override
	public void visit(UBSc e) {
		throwError(e);
	}

	private void throwError(UBEx e) {
		throw new Error("Unexpected instruction at line " + e.myloc);
	}

	@Override
	public void visit(UBSt e) {
		throw new Error("Unexpected string value as input spec at line " + e.myloc);
	}

	private void addressResolver(UBContainer c) {
		if (c.addressModifier) {
			resolvedValue = UBMachine.getLocation(resolvedValue);
		}
	}

}
