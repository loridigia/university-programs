#lunghezza di una lista ricorsivamente
.data
n: 7
v: 3,2,-4,6,-1,5,4

.text
lui $t0, 0x1001 # in t0 lunghezza vettore
lw $t1, 0($t0) # contatore == lunghezza array
lw $t4, 4($t0)
loop:
beq $t1, $zero, exit #se array non vuoto inizia ad analizzarlo altrimenti esci
addi $t1, $t1, -1 #decremento contatore Len. array
lw $t2, 4($t0) # primo valore array
addi $t0, $t0, 4
slt $t3, $t2, $zero # setta t3 a 1 se trova numero negativo
bne $t3, $zero, loop # salta a loop se t3 è 1 cioè è numero negativo
slt $t3, $t2, $t4
beq $t3, $zero, loop
move $t4, $t2
j loop


exit:
move $v1, $t4
li $v0, 5
syscall

      
      



      
      
      
      


             
 
