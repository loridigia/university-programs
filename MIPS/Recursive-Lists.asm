#ritorna la lunghezza della lista ricorsivamente

.text

l: bne $a0, $zero, ric # a0 puntatore alla lista
   li $v0, 0
   jr $ra
   
ric: addi $sp, $sp, -4 # -4 perche StackPointer ( SP == INDIRIZZO DELLO STACK ) funziona al contrario, quindi sale invece che scendere per caricare i dati
     sw $ra, 0($sp)
     lw $a0, 4($sp) #puntatore al successivo elem. della lista
     jal l
     addi $v0, $v0, 1
     lw $ra, 0($sp)
     addi $sp, $sp, 4
     jr $ra
     
