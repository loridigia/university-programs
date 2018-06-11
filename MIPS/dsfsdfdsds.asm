.data 
x: .word 4,5

.text

lui $t0, 0x1001
lw $a0, 0($t0)
lw $a0, 4($t0)

li $v0, 1
syscall
j ciclo

li $v0, 10
syscall