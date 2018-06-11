#!/bin/bash

#Check if $s1 = permission has rights x-w
function isValidDir(){
  local directory=$1
  if [ -d $directory -a -r $directory -a -x $directory ]; then return 0
  else return 1
  fi
}

#assign static values of variables
anno=2018
n_anni=1
added2="false"
added1="false"
error="Uso: $0 [opzioni] matricola D1 d1 d2"

#optional arguments passed to the script
while getopts 'y: n: 1 2' OPTION; do
  case "$OPTION" in
    y)anno="$OPTARG";;
    n)n_anni="$OPTARG";;
    1) if [ $added2 == "true" ]; then
        (>&2 echo $error )
        exit 10
        else added1="true"
        fi;;
    2) if [ $added1 == "true" ]; then
        (>&2 echo $error )
        exit 10
        else added2="true"
        fi;;
    ?) (>&2 echo $error )
       exit 10;;
  esac
done
shift "$(($OPTIND -1))"

#arguments
matr=$1
allDirs=($2 $3 $4 $5)

# check if all arguments are setted (-z) = unset / empty  AND (-o) = or
if [ -z $matr -o ${#allDirs[@]} != 3 ]; then
(>&2 echo $error )
exit 10
fi

#take all elements in array and check if is-a directory and has rights rx
for i in ${allDirs[@]}; do
  if isValidDir $i ; then continue
  else
    (>&2 echo "La directory $i o non esiste o non ha i diritti di lettura/esecuzione")
    exit 100
  fi
done

##
##END OF CONTROLS OF INPUT VALIDITY
##

function isInt() {
    if [[ $1 =~ ^-?[0-9]+$ ]]; then
      return 0
    else return 1
    fi
}

function isDecimal() {
    [[ $1 =~ ^[0-9]+\.[0-9]+$ ]] && ! isInt $1;
}

function total() {
  m1=$1
  m2=$2
  m1m2=$(( $m1 + $m2 ))
  total=$(echo "$m1m2/2" | bc -l )

  if isDecimal $total; then
    int=${total%%.*}
    dec=${total#*.}
    dec=${dec:0:1}
    if [[ $dec < 5 ]]; then
      echo $int
    elif [[ $dec > 5 ]]; then
      int=$(( $int + 1))
      echo $int
    else
      if [[ $(( $int % 2 )) == 0 ]]; then
        echo $int
      else
        int=$(( $int + 1))
        echo $int
      fi
    fi
  else
    echo $int
  fi
}

function cleanArrays(){
  unset matricole
  unset votiInt
  unset votiDecimal
}

function calcolateVotoD1(){
  for int in ${votoI[@]}; do
    totalVotoI=$(( $totalVotoI + $int ))
  done
  for d in ${votoD[@]}; do
    totalVotoD=$(( $totalVotoD + $d ))
  done
  if [[ $totalVotoD > 9 ]]; then
    totalVotoI=$(( $totalVotoI + ${totalVotoD:0:1} ))
    totalVotoD=${totalVotoD:1:2}
  fi
}

function checkVoto(){
  if [[ ${#votoI[@]} == 3 ]]; then
    calcolateVotoD1
  elif [[ ${#votoI[@]} > 3 ]]; then
    newVotoI[0]=${votoI[-3]}
    newVotoI[1]=${votoI[-2]}
    newVotoI[2]=${votoI[-1]}
    votoI=$newVotoI
    calcolateVotoD1
  fi
}

function normalizeData1(){
  old=$1
  M1Y=$(echo $old | cut -f 1 -d "/")
  M1M=$(echo $old | cut -f 2 -d "/")
  M1G=$(echo $old | cut -f 3 -d "/")
  if [[ ${#M1G} < 2 ]]; then
    M1G=0$M1G
  fi
  if [[ ${#M1M} < 2 ]]; then
    M1M=0$M1M
  fi
}

function normalizeData2(){
  old=$1
  M2Y=$(echo $old | cut -f 1 -d "/")
  M2M=$(echo $old | cut -f 2 -d "/")
  M2G=$(echo $old | cut -f 3 -d "/")
  if [[ ${#M2G} < 2 ]]; then
    M2G=0$M2G
  fi
  if [[ ${#M2M} < 2 ]]; then
    M2M=0$M2M
  fi
}

function normalizeDataB(){
  old=$1
  BY=$(echo $old | cut -f 1 -d "/")
  BM=$(echo $old | cut -f 2 -d "/")
  BG=$(echo $old | cut -f 3 -d "/")
  if [[ ${#BG} < 2 ]]; then
    BG=0$BG
  fi
  if [[ ${#BM} < 2 ]]; then
    BM=0$BM
  fi
}


function checkOrale(){
  local file=$1
  local file=${file%'promossi.web'}
  local nDirOrali=$(echo $file | cut -d'/' -f 5 )
  myOrale=$file"orali.txt"
  if [[ -f $myOrale ]]; then
    while read line; do
      matricola=$(echo $line | cut -f 2 -d "|")
      if [ $matricola == $matr ]; then
        echo $(echo $line | cut -f 3 -d "|")
      fi
    done < $myOrale
  fi
}

function findIndexDate(){
  found="false"
  for i in "${!appelli[@]}"; do
    if [[ "${appelli[$i]}" = "${nDirPromossi}" ]]; then
      found="true"
      echo "${i}";
      return
    fi
  done
}

function calcolateYMGM1New(){
  local file=$1
  local file=${file%'promossi.web'}
  local nDirPromossi=$(echo $file | cut -d'/' -f 6 )
  pathF=${file%$nDirPromossi'/'}
  myDate=$pathF"/date.txt"
  if [[ -f $myDate  ]]; then
    appelli=()
    dates=()
    appelli+=($(awk -F':' '{ print $1 }' $myDate ))
    dates+=($(awk -F':' '{ print $2 }' $myDate ))
    index=$(findIndexDate)
    dataEsameM1New=${dates[$index]}
    normalizeData1 $dataEsameM1New
  fi
}

function calcolateYMGM2New(){
  local file=$1
  local file=${file%'promossi.web'}
  local nDirPromossi=$(echo $file | cut -d'/' -f 6 )
  pathF=${file%$nDirPromossi'/'}
  myDate=$pathF"/date.txt"
  if [[ -f $myDate  ]]; then
    appelli=()
    dates=()
    appelli+=($(awk -F':' '{ print $1 }' $myDate ))
    dates+=($(awk -F':' '{ print $2 }' $myDate ))
    index=$(findIndexDate)
    dataEsameM2New=${dates[$index]}
    normalizeData2 $dataEsameM2New
  fi
}

function calcolateYMGMBocc(){
  local file=$1
  local file=${file%'promossi.web'}
  local nDirPromossi=$(echo $file | cut -d'/' -f 6 )
  pathF=${file%$nDirPromossi'/'}
  myDate=$pathF"/date.txt"
  if [[ -f $myDate  ]]; then
    appelli=()
    dates=()
    appelli+=($(awk -F':' '{ print $1 }' $myDate ))
    dates+=($(awk -F':' '{ print $2 }' $myDate ))
    index=$(findIndexDate)
    normalizeDataB ${dates[$index]}
  fi
}


function parziale(){
  local modulo=$1
  local voto=$2
  local day=$3
  local mese=$4
  local anno=$5
  echo "Risultato parziale modulo "$modulo" per la matricola "$matr": "$voto" ("$day"/"$mese"/"$anno")"
}

function parzialeD(){
  local modulo=$1
  local voto=$2
  local decimal=$3
  local day=$4
  local mese=$5
  local anno=$6
  echo "Risultato parziale modulo "$modulo" per la matricola "$matr": "$voto","$decimal" ("$day"/"$mese"/"$anno")"
}

function findMin(){
  local y1=$1
  local y2=$2
  if (( $y1 <= $y2 )); then
    echo $y2
  else echo $y1
  fi
}

function moreRecent(){
  v1=$1
  v2=$2
  if [[ $M1Y > $M2Y ]]; then
    echo $v1
  elif [[ $M2Y > $M1Y ]]; then
    echo $v2
  else
    if [[ $M1M > $M2M ]]; then
      echo $v1
    else
      echo $v2
    fi
  fi
}

function findIndex(){
  value=$matr
  found="false"
  for i in "${!matricole[@]}"; do
    if [[ "${matricole[$i]}" = "${value}" ]]; then
      found="true"
      echo "${i}";
      return
    fi
  done
  if [[ $found=="false" ]]; then
    echo "-1"
  fi
}

function saveRecent(){
  modulo=$1
  if [[ $modulo == 1 ]]; then
    oldVoto=$votoM1New
    oMG=$M1G
    oMM=$M1M
    oMY=$M1Y
  else
    oldVoto=$votoM2New
    oMG=$M2G
    oMM=$M2M
    oMY=$M2Y
  fi
}

function rollBack(){
  if [[ $modulo == 1 ]]; then
    M1Y=$oMY
    M1M=$oMM
    M1G=$oMG
    votoM1New=$oldVoto
  else
    M2Y=$oMY
    M2M=$oMM
    M2G=$oMG
    votoM2New=$oldVoto
  fi
  oMY=0
  oMM=0
  oMG=0
}

function checkRecentData(){
  modulo=$1
  if [[ $modulo == 1 ]]; then
    if [[ $oMY > $M1Y ]]; then
      rollBack
    elif [[ $oMY == $M1Y && $oMM > $M1M ]]; then
      rollBack
    fi
  else
    if [[ $oMY > $M2Y ]]; then
      rollBack
    elif [[ $oMY == $M2Y && $oMM > $M2M ]]; then
      rollBack
    fi
  fi
}

function verifyPreviusVoti(){
  modulo=$1
  if [[ $modulo == 1 ]]; then
    if [[ $BY > $M1Y ]]; then
      unset M1Y
      unset M1M
      unset M1G
      unset votoM1New
    elif [[ $BY == $M1Y && $BM > $M1M ]]; then
      unset M1Y
      unset M1M
      unset M1G
      unset votoM1New
    fi
  else
    if [[ $BY > $M2Y ]]; then
      unset M2Y
      unset M2M
      unset M2G
      unset votoM2New
    elif [[ $BY == $M2Y && $BM > $M2M ]]; then
      unset M2Y
      unset M2M
      unset M2G
      unset votoM2New
    fi
  fi
}

function checkBocciato(){
  local file=$1
  local file=${file%'promossi.web'}
  local nDir=$(echo $file | cut -d'/' -f 5 )
  myBocc=$file"bocciati.txt"
  if [[ -f $myBocc ]]; then
    bocciati=()
    bocciati+=($(awk -F' ' '{ print $1 }' $myBocc ))
    for b in ${bocciati[@]}; do
      if [[ $b == $matr ]]; then
        calcolateYMGMBocc $file
      fi
    done
  fi
}

function resetBocc(){
  unset BY
  unset BM
  unset BG
}


function calcolateYMG(){
  M1Yold=$(echo $fileName | cut -d'_' -f 2 )
  M1Mold=$(echo $fileName | cut -d'_' -f 3 )
  M1Gold=$(echo $fileName | cut -d'_' -f 4 )
  if [[ ${#M1Mold} == 1 ]]; then
    M1Mold=0$M1Mold
  fi
  if [[ ${#M1Gold} == 1 ]]; then
    M1Gold=0$M1Gold
  fi
}

if [[ $added2 == "false" || -z $added1 ]];then
  #analize d1
  promossiM1=()
  promossiM1=$(find ${allDirs[1]} -name '*.web')
  for file in ${promossiM1[@]}; do
    checkBocciato $file
    verifyPreviusVoti 1
    matricole=()
    voti=()
    matricole+=($(awk -F'|' '{ print $2 }' $file ))
    voti+=($(awk -F'|' '{ print $3 }' $file ))
    indexMatr=$(findIndex)
    if [[ $indexMatr != "-1" ]]; then
      saveRecent 1
      votoM1New=${voti[indexMatr]}
      votoCheck=$(checkOrale $file)
      if isInt $votoCheck; then
        votoM1New=$votoCheck
      fi
      calcolateYMGM1New $file
      checkRecentData 1
    fi
  done
  fi

if [[ -z $votoM1New ]]; then
#analize D1
allFiles=()
for file in ${allDirs[0]}/* ; do
  allFiles+=($file)
done
i=0
votoI=()
votoD=()
counter=0
for file in ${allDirs[0]}/* ; do
  cleanArrays
  fileName=$( basename $file)
  fileName=${fileName%'.csv'}
  matricole+=($(awk -F',' '{ print $3 }' $file ))
  votiInt+=($(awk -F',' '{ print $4 }' $file ))
  votiDecimal+=($(awk -F',' '{ print $5 }' $file ))
  k=0
  while [[ $k -le ${#matricole[@]} ]]; do
    if [[ $matr == ${matricole[k]} ]]; then
      if [[ $(echo $fileName | cut -d'_' -f 5 ) == "L1" ]]; then
        if [[ $(echo $fileName | cut -d'_' -f 2 ) -ge $M1Yold ]]; then
          votoI=()
          votoD=()
          votoI+=( ${votiInt[k]} )
          votoD+=( ${votiDecimal[k]} )
        fi
      else
        votoI+=( ${votiInt[k]} )
        votoD+=( ${votiDecimal[k]} )
      fi
      calcolateYMG
    fi
    k=$[$k+1]
  done
done
checkVoto
fi


if [[ $added1 == "false" || -z $added1 ]]; then
#analize d2
resetBocc
promossiM2=()
promossiM2=$(find ${allDirs[2]} -name '*.web')
for file in ${promossiM2[@]}; do
  checkBocciato $file
  verifyPreviusVoti 2
  matricole=()
  voti=()
  matricole+=($(awk -F'|' '{ print $2 }' $file ))
  voti+=($(awk -F'|' '{ print $3 }' $file ))
  indexMatr=$(findIndex)
  if [[ $indexMatr != "-1" ]]; then
    saveRecent 2
    votoM2New=${voti[indexMatr]}
    votoCheck=$(checkOrale $file)
    if isInt $votoCheck; then
      votoM2New=$votoCheck
    fi
    calcolateYMGM2New $file
    checkRecentData 2
  fi
done
fi

#controls at end
if [[ -z $totalVotoI && -z $votoM1New && -z $votoM2New ]]; then
  exit
fi

if [[ $added1 == "true" ]]; then #return only modulo1
  if [[ ! -z $M1Y ]]; then
    sub=$(( $M1Y - $anno ))
    if [[ $sub -le 0 ]]; then
      sub=$(( $sub * -1 ))
    fi
    if [[ $sub -le $n_anni ]]; then
      echo $(parziale 1 $votoM1New $M1G $M1M $M1Y)
    fi
  else
    if [[ $totalVotoD > 0 ]]; then
      echo $(parzialeD 1 $totalVotoI $totalVotoD $M1Gold $M1Mold $M1Yold)
    else
      echo $(parziale 1 $totalVotoI $M1Gold $M1Mold $M1Yold)
    fi
  fi
elif [[ $added2 == "true" ]]; then
  sub=$(( $M2Y - $anno ))
  if [[ $sub -le 0 ]]; then
    sub=$(( $sub * -1 ))
  fi
  if [[ $sub -le $n_anni ]]; then
  echo $(parziale 2 $votoM2New $M2G $M2M $M2Y)
  fi
exit
fi

if [[ ! -z $votoM1New && ! -z $votoM2New ]]; then
  if [[ $M1Y > $anno && $M2Y > $anno ]]; then
    exit
  fi
  if [[ $votoM1New -ge 18 && $votoM2New -ge 18 ]]; then
    sub1=$(($M1Y-$M2Y))
    if [[ $sub1 -le 0 ]]; then
      sub1=$(( $sub1 * -1 ))
    fi
    if [[ $sub1 -le $n_anni ]]; then
      minY=$(findMin $M1Y $M2Y )
      sub=$(( $anno - $minY ))
      if [[ $sub -le 0 ]]; then
        sub=$(( $sub * -1))
      fi
      if [[ $sub -le $n_anni ]]; then
        echo "Risultato finale per la matricola "$matr": "$votoM1New" ("$M1G"/"$M1M"/"$M1Y") + "$votoM2New" ("$M2G"/"$M2M"/"$M2Y") = "$(total $votoM1New $votoM2New)
      fi
    else
      recentV=$(moreRecent $votoM1New $votoM2New )
      sub=$(( $anno - $M1Y ))
      if [[ $sub -le 0 ]]; then
        sub=$(( $sub * -1))
      fi
      if [[ $recentV == $votoM1New && $sub -le $n_anni ]]; then
        echo $(parziale 1 $votoM1New $M1G $M1M $M1Y)
      else
        sub=$(( $anno - $M2Y ))
        if [[ $sub -le 0 ]]; then
          sub=$(( $sub * -1))
        fi
        if [[ $recentV == $votoM2New && $sub -le $n_anni ]]; then
        echo $(parziale 2 $votoM2New $M2G $M2M $M2Y)
        fi
      fi
    fi
  elif [[ $votoM1New -ge 18 ]]; then
    echo $(parziale 1 $votoM1New $M1G $M1M $M1Y)
  else
    echo $(parziale 2 $votoM2New $M2G $M2M $M2Y)
  fi
elif [[ ! -z $votoM1New ]]; then
  sub=$(($anno - $M1Y))
  if [[ $sub -le $n_anni ]]; then
    echo $(parziale 1 $votoM1New $M1G $M1M $M1Y)
  fi
elif [[ $totalVotoI -ge 18 && $(($anno - $M1Yold)) -le $n_anni ]]; then
  sub2=$(($anno - $M2Y))
  sub1=$(($anno - $M1Yold))
  if [[ ! -z $votoM2New && sub1 -le $n_anni && sub2 -le $n_anni ]]; then
    echo "Risultato finale per la matricola "$matr": "$totalVotoI"."$totalVotoD" ("$M1Gold"/"$M1Mold"/"$M1Yold") + "$votoM2New" ("$M2G"/"$M2M"/"$M2Y") = "$(total $totalVotoI $votoM2New)
  elif [[ -z $votoM2New || sub2 > $n_anni ]]; then
    echo $(parzialeD 1 $totalVotoI $totalVotoD $M1Gold $M1Mold $M1Yold)
  fi
else
  sub=$(($anno - $M2Y))
  if [[ $sub -le $n_anni ]]; then
    echo $(parziale 2 $votoM2New $M2G $M2M $M2Y)
  fi
fi
