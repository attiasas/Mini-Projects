@R1
D=M
@number
M=D
@R2
D=M
@power
M=D
@1
D=A
@R0
M=D
@power
D=M
@END_PROGRAM
D;JEQ
@number
D=M
D=D-1
@END_PROGRAM
D;JEQ
@i
M=0
(CHECK_POWER_LOOP)
@i
D=M
@power
D=D-M
@END_POWER_LOOP
D;JGE
@R3
M=0
@j
M=0
(CHECK_MULTIPLY_LOOP)
@j
D=M
@number
D=D-M
@END_MULTIPLY_LOOP
D;JGE
@R0
D=M
@R3
M=M+D
@j
M=M+1
@CHECK_MULTIPLY_LOOP
0;JMP
(END_MULTIPLY_LOOP)
@R3
D=M
@R0
M=D
@i
M=M+1
@CHECK_POWER_LOOP
0;JMP
(END_POWER_LOOP)
(END_PROGRAM)
