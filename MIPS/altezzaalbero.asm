#albero.. funzione ricorsiva che calcola altezza

.data

a: .word 3,b,c
b: .word 4, 0, 0
c: .word 7, d, e
d: .word 21, 0, 0
e: .word 2, 0, 0

.text 
lui $a0, 0x1001

cavallo: bne $a0, $zero, ric
li $v0, 0
jr $ra



ric: 
addi $sp, $sp, -12
sw $ra, 0($sp)
sw $a0, 4($sp)
lw $a0, 4($a0)
jal cavallo
sw $v0, 8($sp)
lw $a0, 4($sp)
lw $a0, 8($a0) 
jal cavallo
lw $t0, 8($sp)
slt $t1, $v0, $t0
beq $t1, $zero, sk  # v0 è gia il piu grande 
move $v0, $t0

sk: addi $v0, $v0, 1
    lw $ra, 0($sp)
    addi $sp, $sp, 12
    jr $ra


#fatto dal prof:





