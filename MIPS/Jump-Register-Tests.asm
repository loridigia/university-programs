.data
n: 5
v: 3,5,6,8,-1

.text

lui $a0, 0x1001
lw $a1, 0($a0)
jal uno
j exit


uno: addi $a1, $a1, 9
     addi $sp, $sp, -4
     sw $ra, 0($sp)
     jal due
          
due: addi $a1, $a1, 1
     lw $ra, ($sp)
     addi $sp, $sp, 4
     jr $ra

exit: 
move $a0, $a1
li $v0, 1
syscall