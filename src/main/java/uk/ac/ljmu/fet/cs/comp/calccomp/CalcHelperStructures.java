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
package uk.ac.ljmu.fet.cs.comp.calccomp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.FunctionDeclarationStatement;
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.VariableRef;

public class CalcHelperStructures {
	public static final FunctionDeclarationStatement globalFunction = new FunctionDeclarationStatement(-1,
			new VariableRef(-1, "gLoBaLfUnCtIoN"), null, null);
	public static final List<FunctionDeclarationStatement> allFunctions = new ArrayList<>();
	/**
	 * Contains the mapping of the overloaded function names to their actual
	 * alternative declarations
	 */
	public static final HashMap<String, Set<FunctionDeclarationStatement>> alternatives = new HashMap<>();

	static {
		globalFunction.target.functionName = true;
		allFunctions.add(globalFunction);
		HashSet<FunctionDeclarationStatement> globalAlternativeList = new HashSet<>();
		globalAlternativeList.add(globalFunction);
		alternatives.put(globalFunction.target.myId, Collections.unmodifiableSet(globalAlternativeList));
	}
}
