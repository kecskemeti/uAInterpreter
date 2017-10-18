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

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

//TODO: The original parser, not yet compatible with the new interpreter
public class BaseParser {
	public static final HashMap<String, Integer> constMap = new HashMap<>();
	public static final HashMap<String, Step> labels = new HashMap<>();
	public static final HashMap<Integer, Step> theProgram = new HashMap<>();
	private static String currentLabel = null;

	public static void errorAndExit(String msg) {
		System.err.println("Fatal error at line " + (UAMachine.programCounter + 1));
		System.err.println(msg);
		System.exit(1);
	}

	static enum Register {
		A, B, C, D;
		public int data = 0;

		public static Register getByOrdial(int ordial) {
			for (Register r : EnumSet.allOf(Register.class)) {
				if (r.ordinal() == ordial) {
					return r;
				}
			}
			errorAndExit("Invalid register spec");
			return null;
		}

		public static Register fromString(String v) {
			try {
				return Register.valueOf(v);
			} catch (IllegalArgumentException e) {
				errorAndExit("Invalid register identifier");
				return null;
			}

		}

		@Override
		public String toString() {
			return "Register " + name() + " value: " + data;
		}
	}

	static class ParSet {
		private final boolean regA;
		public final Register B;
		private final String A;

		public ParSet(boolean isAReg, String A, Register B) {
			this.regA = isAReg;
			this.A = A;
			this.B = B;
		}

		public int resolveA() {
			int par = -1;
			if (regA) {
				par = Register.fromString(A).data;
			} else {
				// Late binding to labels
				try {
					par = Integer.parseInt(A);
				} catch (NumberFormatException nf) {
					// Not a number, maybe a label or a constant
					Integer addr = constMap.get(A);
					if (addr == null) {
						Step s = labels.get(A);
						if (s == null) {
							errorAndExit("Invalid constant use in the operation's parameter");
						} else {
							par = s.myloc;
						}
					} else {
						par = addr;
					}
				}
			}
			return par;
		}

		@Override
		public String toString() {
			return "ParSet: " + (regA ? "R" : "C") + A + " " + B;
		}
	}

	static class Step {
		private final Operation op;
		private final ParSet p;
		public final String label;
		public final int myloc;

		public Step(Operation o, String opDetails) {
			if (currentLabel != null) {
				label = currentLabel;
				currentLabel = null;
				labels.put(label, this);
			} else {
				label = null;
			}
			myloc = UAMachine.programCounter;
			op = o;
			ParSet t = null;
			String[] pars = opDetails.substring(1).split(",");
			boolean isParReg = false;
			switch (opDetails.charAt(0)) {
			case 'C':
				break;
			case 'R':
				isParReg = true;
				break;
			default:
				errorAndExit("Invalid mode of operation");
			}
			t = new ParSet(isParReg, pars[0].trim(),
					pars.length < 2 ? Register.A : Register.fromString(pars[1].trim()));
			p = t;
		}

		public void execute() {
			op.run(p);
		}

		@Override
		public String toString() {
			return "Step: " + op + " Label: " + label + " Loc: " + myloc + " " + p;
		}
	}

	static enum Operation {
		LD {
			@Override
			public void run(ParSet p) {
				p.B.data = UAMachine.getLocation(p.resolveA());
			}
		},
		ST {
			@Override
			public void run(ParSet p) {
				UAMachine.setLocation(p.resolveA(), p.B.data);
			}
		},
		JZ {
			@Override
			public void run(ParSet p) {
				if (p.B.data != 0) {
					JM.run(p);
				}
			}
		},
		JM {
			@Override
			public void run(ParSet p) {
				UAMachine.programCounter = p.resolveA();
				UAMachine.programCounter--;
			}
		},
		AD {
			@Override
			public void run(ParSet p) {
				doArt(p, ArtOp.AD);
			}
		},
		ML {
			@Override
			public void run(ParSet p) {
				doArt(p, ArtOp.ML);
			}
		},
		DV {
			@Override
			public void run(ParSet p) {
				doArt(p, ArtOp.DV);
			}
		},
		MV {
			@Override
			public void run(ParSet p) {
				p.B.data = p.resolveA();
			}
		};
		public abstract void run(ParSet p);

		public void doArt(ParSet p, ArtOp op) {
			p.B.data = op.realOP(p.B.data, p.resolveA());
		}

	}

	public static void main(String[] args) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(args[0], "r");
		ArrayList<String> fc = new ArrayList<>();
		String l;
		int constIndex = UAMachine.constants;
		// Reading the file and processing the constants
		while ((l = raf.readLine()) != null) {
			String tr = l.trim();
			if (tr.startsWith(";")) {
				tr = "";
			}
			fc.add(tr);
			if (!tr.isEmpty()) {
				// Content
				if (tr.startsWith("CON")) {
					String[] spaceSplit = tr.split("\\s+");
					if (spaceSplit.length > 2) {
						String constantName = spaceSplit[1];
						switch (tr.substring(3, 6)) {
						case "ST ":
							// Constant name + trailing space
							String data = l.substring(l.indexOf(constantName) + constantName.length() + 1);
							constMap.put(constantName, constIndex);
							boolean skipInitial = true;
							int stlen = 0;
							for (int i = 0; i < data.length(); i++) {
								char c = data.charAt(i);
								if (skipInitial) {
									if (Character.isSpaceChar(c)) {
										continue;
									} else {
										skipInitial = false;
									}
								}
								UAMachine.setConstant(constIndex++, c);
								stlen++;
							}
							if (stlen == 0) {
								errorAndExit("Empty string constant");
							}
							UAMachine.setConstant(constIndex, 0);
							break;
						case "NR ":
							try {
								int num = Integer.parseInt(spaceSplit[2]);
								UAMachine.setConstant(constIndex, num);
								constMap.put(constantName, constIndex);
							} catch (NumberFormatException nf) {
								errorAndExit(nf.getMessage());
							}
							break;
						default:
							errorAndExit("Invalid constant type.");
						}
						constIndex++;
						// Constants are processed now we don't need to remember them
						fc.remove(UAMachine.programCounter);
						fc.add("");
					} else {
						errorAndExit("Invalid constant definition.");
					}
				}

			}
			UAMachine.programCounter++;
		}
		raf.close();
		UAMachine.programCounter = 0;
		// Parsing the instructions
		for (String tr : fc) {
			if (!tr.isEmpty()) {
				int labEnd = tr.indexOf(':');
				if (labEnd > 0) {
					if (labEnd + 1 != tr.length()) {
						errorAndExit("Invalid label");
					}
					currentLabel = tr.substring(0, tr.length() - 1);
				} else {
					String opid = tr.substring(0, 2);
					try {
						Step s = new Step(Operation.valueOf(opid), tr.substring(2));
						theProgram.put(UAMachine.programCounter, s);
					} catch (IllegalArgumentException e) {
						errorAndExit("Unknown operation name");
					}
				}
			}
			UAMachine.programCounter++;
		}
		Step start = labels.get("entry");
		Step stop = labels.get("exit");
		if (start == null) {
			errorAndExit("No program entry label is defined");
		}
		if (stop == null) {
			errorAndExit("No program termination label is defined");
		}
		UAMachine.programCounter = start.myloc;
		UAMachine.finalProgramAddress = stop.myloc;
		// Parsing complete. Here comes the GUI
	}
}
