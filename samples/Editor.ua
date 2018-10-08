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

; A simple text editor
; Behaviour:
;  - due to limitations of the "ua machine" you cannot have repeated keystrokes
;       of the same key. To achieve the effect, use a non-visible key in between
;       two keystrokes that were intended to be repeated 
;  - shows the cursor by alternating the cursor chars and the current
;       content of the text at the cursor's location
;  - arrow keys allow moving around the text
;  - if the cursor reaches the top or the bottom of the screen, then scrolls
;       the content in the direction of the pressed arrow
;  - when a visible character is pressed >32 <127 the character is shown at
;       the current cursor location and the cursor is moved to the right
;  - if the cursor reaches the end of the line it just stays there (i.e., 
;       there could be no more than 80 chars per line)
;  - as there is no disk in uA, you cannot store the data to it.. anything you
;       write will be lost once you close the interpreter's window.

; Memory layout of the program:
; 10000-19999 (5 full pages of text)
; 0-1999 the current view - i.e., the screen
; 20000 the position of the current view e.g., 10000 for the topmost line,
;			10080 for the next
; 20001 the position of the cursor on the screen i.e., 0-1999
; 20003 pointer to the current cursor shape
; 20004 cursor blink: if 1 then cursor shape is show, if 0 then the actual
;			content 
; 25000 register A is saved here by functions
; 25001 register B is saved here by functions
; 25002 register C is saved here by functions
; 25003 register D is saved here by functions

; Relevant key codes:
; Enter: 10
; ESC: 27
; Left arrow: 37
; Up arrow: 38
; Right arrow: 39
; Down arrow: 40

; This sample shows features like:
; 1. Function calls with return locations pushed via a register. 
;      see the label bufferedtocursor
; 2. Modulo 80, shown just before the exit label
; 3. "Switch" statement check the consecutive jumps for labels:
;   a) notEnter
;   b) notESC
;   c) notLeftArrow
;   d) notUpArrow
;   e) notRightArrow
;   f) notDownArrow
; 4. Demonstrates how the JZC conditional jump can be used for
;      a wide variety of conditions (like lower than, bigger than
;      or even equals)
; 5. Shows how to simply save register state in between function
;      calls. WARNING: this does not allow nested function calls!
;      See the labels visualiseCursor, dodelay, bufferedTocursor

CONST cursorShape -/|\
CONNR delay 400000


entry:
; Setup the initial cursorshape
MVC cursorShape,A
STC 20003,A
MVC 10000,A
STC 20000,A
MVC 0,A

mainLoop:
MVR A,B
; A has the new value read from the keyboard, we must preserve A until the start of the next main loop
LDC 2000,A
; CHECK IF IT IS DIFFERENT THAN BEFORE
MVR A,C
MLC -1,C
ADR C,B
JZC change,B
JMC visualiseCursor
change:
MVR A,B
ADC -10,B
JZC notEnter, B
; Enter was hit
; Query our location
LDC 20001, B
DVC 80,B
; Increase the linecount
ADC 1,B
MLC 80,B
STC 20001, B
JMC checkLocationValidity
notEnter:
ADC -17,B
JZC notESC,B
JMC exit
notESC:
ADC -10,B
JZC notLeftArrow,B
; Left arrow was hit
MVC leftArrow,D
JMC bufferedTocursor
leftArrow:
LDC 20001, B
ADC -1,B
STC 20001, B
ADC 1,B
MVR B,C
DVC 80,B
MLC -80,B
ADR B,C
JZC visualiseCursor,C
;Reached the left side, move back cursor where it was
LDC 20001,B
ADC 1,B
STC 20001,B
JMC visualiseCursor
notLeftArrow:
ADC -1,B
JZC notUpArrow,B
; Up arrow was hit
MVC upArrow,D
JMC bufferedTocursor
upArrow:
LDC 20001, B
ADC -80,B
STC 20001, B
JMC checkLocationValidity
notUpArrow:
ADC -1,B
JZC notRightArrow,B
; Right arrow was hit
MVC rightArrow,D
JMC bufferedTocursor
rightArrow:
LDC 20001, B
ADC 1,B
STC 20001, B
MVR B,C
DVC 80,B
MLC -80,B
ADR B,C
JZC visualiseCursor,C
;Reached the right side, move back cursor where it was
LDC 20001,B
ADC -1,B
STC 20001,B
JMC visualiseCursor
notRightArrow:
ADC -1,B
JZC notDownArrow,B
; Down arrow was hit
MVC downArrow,D
JMC bufferedTocursor
downArrow:
LDC 20001, B
ADC 80,B
STC 20001, B
JMC checkLocationValidity
notDownArrow:
MVR A,B
DVC 41,B
JZC notLowControl,B
; Some control chars which we don't print
MVR A,B
ADC -32,B
JZC visualiseCursor,B
; we have a space
JMC safeToPrint
notLowControl:
MVR A,B
DVC 96,B
; Check if high value control chars are present, don't print them
JZC visualiseCursor,B

safeToPrint:
; We are safe to print
; Print to screen
LDC 20001,B
STR B,A
; Save it into our buffer
LDC 20000,C
ADR C,B
STR B,A
; advance to the next position
LDC 20001,B
ADC 1,B
; Modulo
MVR B,C
DVC 80,C
MLC -80,C
ADR B,C
; C now has B modulo 80
JZC storeNewLoc,C
; modulo is 0, we are at the end, stay there
ADC -1,B
storeNewLoc:
STC 20001,B
JMC visualiseCursor
exit:
JMC exit

checkLocationValidity:
LDC 20001,B
; check if we are over 2000
MVR B,C
DVC 2000,C
JZC biggerThan,C
;was it positve?
MVR B,C
ADC 2000,C
DVC 2000,C
JZC visualiseCursor,C
; No it was not, we need to scroll up a line
; first set the cursor back to where it was
LDC 20001,B
ADC 80,B
STC 20001,B
; next move the buffer up a line
LDC 20000,B
; save original buffer pos
MVR B,C
ADC -80,B
; Store the updated
STC 20000,B
; Did we get out of the buffer?
DVC 10000,B
JZC repaintbuffer,B
; Yes we did, so let's reset the buffer to the top
STC 20000,C
; As we did not change the buffer, we can go to cursor
JMC visualiseCursor 
biggerThan:
; We need to scroll down a line
; first set the cursor back to where it was
LDC 20001,B
ADC -80,B
STC 20001,B
; next move the buffer down a line
LDC 20000,B
; save original buffer pos
MVR B,C
ADC 80,B
; Store the updated
STC 20000,B
; Did we reach out of the buffer?
ADC -20000, B
JZC repaintbuffer,B
; Yes we did, so let's reset the buffer to the bottom
STC 20000,C
; As we did not change the buffer, we can go to cursor
JMC visualiseCursor 

; Copies everything from buffer to screen
; This is an expensive operation, does a complete refresh
; only needed if we move buffer location around
repaintbuffer:
; copy positions stored in B, C
MVC 0,C
LDC 20000,B
repaintloop:
; copy from buffer to screen
LDR B,D
STR C,D
; buffer position increase
ADC 1,C
ADC 1,B
MVR C,D
ADC -2000,D
JZC repaintloop,D
;repaint complete, continue on visualising


; shows the cursor at the current location
; uses registers A,B,C
visualiseCursor:
; stores the used regs
STC 25000,A
STC 25001,B
STC 25002,C
; Checks what to show
LDC 20004,A
JZC doCursor,A
; doActualContent
; Calc where the cursor is in the storage, address ends up in B
LDC 20000,A
LDC 20001,B
ADR A,B
; Address on what to write out is now in A
LDR B,A
; Makes sure we show the cursor next time
MVC 1,B
STC 20004,B
; We are ready to show the content
JMC updateCursorView
doCursor:
; Remember to do actual content next time
MVC 0,B
STC 20004,B
; Load the cursor shape instead of the content
LDC 20003,B
; A will have the cursor shape, we should keep the register untouched
LDR B,A
; change shape
ADC 1,B
STC 20003,B
; check if the change needs a reset
MVC cursorShape,C
; 4= len of cursorShape
ADC 4,C
MLC -1,C
ADR C,B
JZC updateCursorView,B
; We reached the last cursor symbol, we reset the location
MVC cursorShape,C
STC 20003,C
updateCursorView:
; We expect A to contain what to show on location
LDC 20001,B
; The actual cursor is shown on screen now:
STR B,A
;restores the used regs
LDC 25000,A
LDC 25001,B
LDC 25002,C
; proceeds to delay

; Uses the register A
; Delaying the next cursor drawing
; Does busy waiting (as there is no chance to use a timer)
dodelay:
; save the original contents of A
STC 25000,A
LDC delay,A
indelay:
MVR A,A
ADC -1,A
MVR A,A
JZC indelay, A
; restore the original contents of A
LDC 25000,A
JMC mainLoop

; Repaint current cursor location
; Uses A,B (saves and restores them)
; Receives return address in D
bufferedTocursor:
STC 25000,A
STC 25001,B
; Calculates the buffer address
LDC 20000,A
LDC 20001,B
ADR A,B
; Loads buffered character
LDR B,A
; Loads cursor position
LDC 20001,B
; Stores buffered char at cursor
STR B,A
; Restores A,B
LDC 25000,A
LDC 25001,B
JMR D