#! /usr/bin/awk -f

function checkH(str) {
  return str~/:/
}

function isNull(str) {
  return (str == "")
}

function isInt(matricola) {
  return matricola ~ /^[0-9]+$/
}

function countTurni() {
  #calcolo turni
  while ( turni==0 ){
    if ( $(2*count) != ""){
      count++
    }
    else {
      turni=count-1
    }
  }
}

function checkIfAdded(i,matricola2) {
  for (row = arrayIndex[i]; row < arrayIndex[i+1]; row++){
    for ( t = 1; t <= turni; t++){
      if ( array[row, t] == matricola2 ) return 0
    }
  }
  return 1
}

function pushPresent(matr) {
  arrayPresent[indexP]=matr
  indexP++
}

function pushSpostate(matr,t,t2) {
  arraySpostate[indexS]=matr
  arrayTurnoBefore[indexS]=t
  arrayTurnoAfter[indexS]=t2
  indexS++
}

function pushCancellate(matr) {
  arrayCancellate[indexC]=matr
  indexC++
}

function createOutput() {
  #output for moved
  s = asort(arraySpostate, sortedSpostate)
  for ( k=0; k<=length(arraySpostate); k++){
    for ( j=0; j<length(arraySpostate); j++){
      if ( sortedSpostate[k] == arraySpostate[j]){
        printf( "La matricola "arraySpostate[j]" e' stata spostata dal turno "arrayTurnoBefore[j]" al turno "arrayTurnoAfter[j]" nel passare dalla versione "i" alla versione " i+1"\n")
      }
      }
    }
  c = asort(arrayCancellate)
  for ( k=1; k<=length(arrayCancellate); k++){
    printf( "La matricola "arrayCancellate[k]" e' stata cancellata nel passare dalla versione "i" alla versione "i+1"\n")
  }
  n = asort(arrayAdded)
  for ( k=1; k<=length(arrayAdded); k++){
    printf( "La matricola "arrayAdded[k]" e' stata aggiunta nel passare dalla versione "i" alla versione "i+1"\n")
  }
  return 1
}

function nextFile() {
  findAdded()
  createOutput()
  i++
  delete arraySpostate
  delete arrayCancellate
  delete arrayAdded
  delete arrayPresent
  delete arrayTurnoBefore
  delete arrayTurnoAfter
  indexS=0
  indexC=0
  indexA=0
}

function findAdded() {
  indexA=0
  for ( r = arrayIndex[i+1]; r < arrayIndex[i+2]; r++){
    for ( tt = 1; tt <= turni; tt++){
      added = 1
      for ( p = 0; p < length(arrayPresent); p++){
        if ( array[r, tt] == arrayPresent[p] ) added=0
      }
      if ( added && isInt(array[r, tt]) ) {
        arrayAdded[indexA] = array[r, tt]
        indexA++
      }
    }
  }
}


BEGIN {
  turni=0
  count=1
  file=1
}
{
  if ( NR == 1 ) countTurni();

  #create matrix
  if ( ! checkH( $0 ) && ! isNull( $0 )){
    turno=1
    while (turno <= turni){
      matricola=$(2*turno)
      if ( isInt(matricola)) array[NR,turno]=matricola
      turno++
    }
    eof=NR
  }
  else if ( checkH( $0 ) ){
    arrayIndex[file]=NR+1
    file++
    next
  }

}
END {
  indexS=0
  indexC=0
  indexP=0
  aggiunte=0
  arrayIndex[file] = eof+1
  i = 1

  for (row=2; row <= arrayIndex[length(arrayIndex)]; row++){
    if ( row == arrayIndex[i+1] ) nextFile()
    for (t = 1; t <= turni; t++){
      found=0
      matricola1 = array[row, t]
      if ( ! isInt(matricola1) ) break
      pushPresent( matricola1 )
      for ( row2 = arrayIndex[i+1]; row2 < arrayIndex[i+2]; row2++){
        for (t2 = 1; t2 <= turni; t2++){
          matricola2 = array[row2, t2]
          if ( ! isInt(matricola2) ) break
          else if ( matricola1 == matricola2 ) {
            found=1
            if ( t != t2) pushSpostate(matricola1, t, t2)
          }
        }
      }
      if ( found == 0 ) pushCancellate(matricola1)
    }
  }
}
