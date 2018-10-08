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
package uk.ac.ljmu.fet.cs.comp.interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.CodeLabel;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Expression;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Identifier;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.IntNumber;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.NumberConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Operation;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.Register;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringConstant;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.StringValue;

public class BaseParser {
	int lineCount=-1;

	private void throwError(String text, Throwable e) {
		throw new Error(text + " at line " + lineCount, e);
	}

	private void throwError(String text) {
		throwError(text, null);
	}

	BufferedReader reader;
	boolean inprocessing = true;

	public BaseParser(FileReader r) {
		reader = new BufferedReader(r);
	}

	public Expression yylex() throws IOException {
		if (inprocessing) {
			String l;
			// Reading the file and processing the constants
			while ((l = reader.readLine()) != null) {
				lineCount++;
				String tr = l.trim();
				if (tr.startsWith(";")) {
					// Comments
					tr = "";
				}
				if (!tr.isEmpty()) { // Non whitespace
					// Content
					if (tr.startsWith("CON")) {
						// Constants
						String[] spaceSplit = tr.split("\\s+");
						if (spaceSplit.length > 2) {
							String constantName = spaceSplit[1];
							Identifier constId = new Identifier(lineCount, constantName);
							switch (tr.substring(3, 6)) {
							case "ST ":
								// Constant name + trailing space
								return new StringConstant(lineCount, constId, new StringValue(lineCount,
										l.substring(l.indexOf(constantName) + constantName.length() + 1)));
							case "NR ":
								try {
									return new NumberConstant(lineCount, constId,
											new IntNumber(lineCount, spaceSplit[2]));
								} catch (NumberFormatException nf) {
									throwError("Illegal number constant value");
								}
								break;
							default:
								throwError("Invalid constant type.");
							}
						} else {
							throwError("Invalid constant definition.");
						}
					} else {
						int labEnd = tr.indexOf(':');
						if (labEnd > 0) {
							// Labels
							if (labEnd + 1 != tr.length()) {
								throwError("Invalid label");
							}
							return new CodeLabel(lineCount, new Identifier(lineCount, tr.substring(0, tr.length() - 1)),
									null);
						} else {
							// Instructions
							String opN = tr.substring(0, 2);
							String opDetails = tr.substring(2);
							try {
								String[] pars = opDetails.substring(1).split(",");
								Operation.AttKind kind = Operation.AttKind.valueOf("" + opDetails.charAt(0));
								String leftStr = pars[0].trim();
								Expression left = null;
								try {
									// Test if it is a reg
									Register.RegType.valueOf(leftStr);
									left = new Register(lineCount, leftStr);
								} catch (IllegalArgumentException e) {
									// Exception if this is not reg
									try {
										left = new IntNumber(lineCount, leftStr);
									} catch (NumberFormatException nf) {
										// Exception if it is not a number
										left = new Identifier(lineCount, leftStr);
									}
								}
								Register right = new Register(lineCount,
										pars.length < 2 ? Register.RegType.A.name() : pars[1].trim());
								return Operation.opFactory(lineCount, opN, left, right, kind);
							} catch (IllegalArgumentException e) {
								throwError("Illegal input type spec");
							} catch (Exception e) {
								// OpFactory fails
								throwError("Illegal operation ", e);
							}
						}
					}

				}
			}
			// Parsing complete
			inprocessing = false;
			reader.close();
		}
		return null;
	}
}
