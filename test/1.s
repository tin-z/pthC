.text
.global _start

_start:

F1:
 movl $4, %edx
 movl $intA, %ecx
 movl $0, %ebx
 movl $3, %eax
 int $0x80

F2: 
 movl $4, %edx
 movl $intB, %ecx
 movl $0, %ebx
 movl $3, %eax
 int $0x80

F3: 
 movl $1, %eax
 int $0x80

.data
 intA: .byte 0,0,0,0
 intB: .byte 0,0,0,0
