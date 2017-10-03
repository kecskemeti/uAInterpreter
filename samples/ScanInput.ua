;
;  ========================================================================
;  uA Interpreter
;  ========================================================================
;  
;  This file is sample source code for the ua Interpreter.
;  
;  ua Interpreter is free software: you can redistribute it and/or
;  modify it under the terms of the GNU General Public License as published
;  by the Free Software Foundation, either version 3 of the License, or (at
;  your option) any later version.
;  
;  ua Interpreter is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of 
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
;  General Public License for more details.
;
;  You should have received a copy of the GNU General Public License along
;  with ua Interpreter.  If not, see <http://www.gnu.org/licenses/>.
;  
;  (C) Copyright 2017, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
;

; Constantly scans for user input and puts it on the top left corner of the screen

entry:
MVR A,A
READINPUT:
LDC 2000,A
; CHECK IF IT IS DIFFERENT THAN BEFORE
MVR A,C
MLC -1,C
MVR B,D
ADR C,D
JZC CHANGE,D
JMC READINPUT
CHANGE:
; PRINTOUT
STC 0,A
MVR A,B
JMC READINPUT
exit:
JMC exit