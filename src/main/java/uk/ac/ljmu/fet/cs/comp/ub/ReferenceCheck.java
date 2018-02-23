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
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBEx;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBId;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBInt;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBLab;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBNr;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBReg;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSc;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSt;

public class ReferenceCheck implements Visitor {

	@Override
	public void visit(UBId i) {
		UBEx ex = SymbolTable.globalTable.get(i.containedValue);
		if (ex == null) {
			throw new Error("Unknown identifier referenced at line " + i.myloc);
		}
		i.setMemLoc(((UBId) ex.left).getMemLoc());
	}

	private void compositeDescent(UBEx e) {
		if (e.left != null) {
			e.left.accept(this);
		}
		if (e.right != null) {
			e.right.accept(this);
		}
	}

	@Override
	public void visit(UBInt e) {
		compositeDescent(e);
	}

	@Override
	public void visit(UBReg e) {
		compositeDescent(e);
	}

	@Override
	public void visit(UBSt e) {
		compositeDescent(e);
	}

	@Override
	public void visit(UBNr e) {
		compositeDescent(e);
	}

	@Override
	public void visit(UBSc e) {
		compositeDescent(e);
	}

	@Override
	public void visit(UBLab e) {
		compositeDescent(e);
	}

	@Override
	public void visit(ADDIInst e) {
		compositeDescent(e);
	}

	@Override
	public void visit(DIVIInst e) {
		compositeDescent(e);
	}

	@Override
	public void visit(JUMPInst e) {
		compositeDescent(e);
	}

	@Override
	public void visit(CONDInst e) {
		compositeDescent(e);
	}

	@Override
	public void visit(MULIInst e) {
		compositeDescent(e);
	}

	@Override
	public void visit(MOVEInst e) {
		compositeDescent(e);
	}
}
