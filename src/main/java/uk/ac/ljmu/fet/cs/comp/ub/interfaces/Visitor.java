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
package uk.ac.ljmu.fet.cs.comp.ub.interfaces;

import uk.ac.ljmu.fet.cs.comp.ub.tokens.ADDIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.CONDInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.DIVIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.JUMPInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.MOVEInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.MULIInst;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBId;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBInt;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBLab;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBNr;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBReg;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSc;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.UBSt;

public interface Visitor {
	void visit(ADDIInst e);

	void visit(UBLab e);

	void visit(DIVIInst e);

	void visit(UBId e);

	void visit(UBInt e);

	void visit(JUMPInst e);

	void visit(CONDInst e);

	void visit(MULIInst e);

	void visit(MOVEInst e);

	void visit(UBNr e);

	void visit(UBReg e);

	void visit(UBSc e);

	void visit(UBSt e);
}
