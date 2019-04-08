#calcola lunghezza lista

.data

a: .word 1, k
k: .word 3, c
c: .word 5, 0


.text

lungh:
li $v0, 0
beq $a0, $zero, exit
loop: lw $a0, 4($a0)
addi $v0, $v0, 1
bne $a0, $zero, loop

exit:
jr $ra