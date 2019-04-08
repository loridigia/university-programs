.data
n: .word 5
v: 3,5,-1,2,9

.text

lui $t0, 0x1001     #t0 carico indirizzo base, puntatore 
lw $a1, 0($t0) #a0, lunghezza vettore
loop: beq $a1, $zero, exit #se lunghezza vettore == 0, salta a exit
addi $t0, $t0, 4   #incremento puntatore
lw $t1, 0($t0)  #carico elemento in t1
addi $a1, $a1, -1 #decremento contatore
slt $t2, $t1, $zero
bne $t2, $zero, loop
add $v0, $v0, $t1
j loop
exit:
move $a0, $v0
li $v0, 1
syscall 


