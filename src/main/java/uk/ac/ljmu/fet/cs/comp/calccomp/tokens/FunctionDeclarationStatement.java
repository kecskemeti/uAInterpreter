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
package uk.ac.ljmu.fet.cs.comp.calccomp.tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ljmu.fet.cs.comp.calccomp.CalcHelperStructures;
import uk.ac.ljmu.fet.cs.comp.calccomp.interfaces.CalcVisitor;

public class FunctionDeclarationStatement extends Statement {
	public static final String canonicalSeparator = "0CASE0TYPE";
	public static final int returnValueIndicator = 1;
	public AlterScope scopeOpensAt = null;
	public AlterScope scopeClosesAt = null;
	public HashMap<String, VariableRef> inScopeVars = new HashMap<>();
	public ArrayList<Statement> inScopeStatements = new ArrayList<>();
	private VariableRef canonicalName = null;
	// frameIndex 0 = return address, frameIndex 1 = return value
	private int frameIndex = 2;

	public FunctionDeclarationStatement(int loc, VariableRef target, CalcExpression left, CalcExpression right) {
		super(loc, target, left, right);
	}

	public void generateCanonicalName() {
		if (canonicalName == null) {
			canonicalName = new VariableRef(target.myloc, (target.myId + canonicalSeparator
					+ (left instanceof VariableRef ? "G" : ((CalcIntNumber) left).number)).toLowerCase());
			canonicalName.functionName = true;
			Set<FunctionDeclarationStatement> alts = CalcHelperStructures.alternatives.get(target.myId);
			if (alts == null) {
				alts = new HashSet<>();
				CalcHelperStructures.alternatives.put(target.myId, alts);
			}
			alts.add(this);
		}
	}

	public VariableRef getCanonicalName() {
		return canonicalName == null ? target : canonicalName;
	}

	/**
	 * Registers the variable to be in our scope and remembers a frame pointer for
	 * it
	 * 
	 * @param v
	 * @return
	 */
	public boolean addVarInScope(VariableRef v) {
		if (!inScopeVars.containsKey(v.myId)) {
			// First assignment
			inScopeVars.put(v.myId, v);
			if (v.myId.equals(target.myId)) {
				// Handles assignments to function names
				// i.e., return values
				v.memLoc = returnValueIndicator;
			} else if (!v.functionName) {
				// Assigns the frame location
				v.memLoc = frameIndex++;
			}
			return true;
		}
		return false;
	}

	public int getFrameIndex() {
		return frameIndex;
	}

	@Override
	public void accept(CalcVisitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return "(FUNCDEF " + target + " PAR " + left + ")";
	}

}
