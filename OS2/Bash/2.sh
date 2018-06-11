#!/usr/bin/env bash

#take all compressed files
function takeAll() {
  allCompressed=$(find $dir -name '*.zip' -o -name '*.tgz' -o -name '*.gz' -o -name '*.tbz' -o -name '*.bz' -o -name '*.bz2' -o -name '*.tar')
}


#return path of file given
function getPath {
  local path=${1%/*}
  echo $path
}

dir="."

#check if arguments are passed if more than one return error
if [ ! -z $1 ]; then dir=$1
  if [ ! -z $2 ]; then
    echo "Uso: s [dir]"
    exit 10
  fi
fi

#check if is-a directory and has readble right
if [ -d $dir ]; then
  if [ ! -r $dir ]; then
    echo "Impossibile leggere la directory $dir"
    exit 30
  fi
else
  echo "La directory d non esiste"
  exit 20
fi

#main engine
function main {
  local extracted=("$@")
  local baseCompressed=$( basename $file )
  local nameCompressedNoExt=${baseCompressed%$format}
  local newNameExtracted=$baseCompressed.$extracted
  exLength=${#extracted[*]}
  #first point of the text
  if [ $exLength == 1 -a ! -d $path/$extracted ]; then
    mv $path/$extracted $path/$newNameExtracted
    rm $file
  #second point of the text
  elif [ $exLength == 1 -a -d $path/$extracted ]; then
    mv $path/$extracted $path/$newNameExtracted
    mv $file $path/$newNameExtracted/
  #third point of the text
  else
    mkdir -p $path/$nameCompressedNoExt
    for i in "${extracted[@]}"; do
      mv $path/$i $path/$nameCompressedNoExt/
    done
    mv $file $path/$nameCompressedNoExt/
  fi
  extracted=()
}

#return extratedFiles in global var arrayAfter
function extratedFiles {
  extracted=()
  for a in "${arrayAfter[@]}"; do
    local found="false"
    for b in "${arrayBefore[@]}"; do
      if [[ $a == $b ]]; then
        found="true"
      fi
    done
    if [[ $found == "false" ]]; then
      extracted+=($a)
    fi
  done
}

#start elaborate and call the main engine to elaborate it
function elaborate1 {
  file=$1
  path=0
  path=$( getPath "$file" )
  arrayBefore=()
  arrayBefore=(` ls -A "$path" `)
}
function elaborate2 {
  arrayAfter=()
  arrayAfter=(` ls -A "$path" `)
  extratedFiles
  main ${extracted[*]}
}

takeAll $dir

for i in $allCompressed; do #adding if[-d ] ( perche alcune cartelle hanno estenzione)
  if [ ! -d $i ]; then
  bname=$(basename -- "$i")
  #compressed zip
  if [ ${bname: -3} == "zip" ]; then
    format=".zip"
    elaborate1 $i
    unzip -q $file -d $path
    elaborate2
    #compressed gz
  elif [ ${bname: -2} == "gz" -a ${bname: -6} != "tar.gz" -a ${bname: -3} != "tgz" ]; then
    format=".gz"
    elaborate1 $i
    gzip -d -k $file
    elaborate2
  #compressed bz2
  elif [ ${bname: -2} == "bz" -a ${bname: -3} != "tbz" -a ${bname: -2} == "bz" -a ${bname: -6} != "tar.bz" -o ${bname: -3} == "bz2" -a ${bname: -7} != "tar.bz2" ]; then
    if [ ${bname: -2} == "bz" ]; then
      format=".bz"
    else
      format=".bz2"
    fi
    elaborate1 $i
    bzip2 -d -k $file
    elaborate2
  #compressed tar
  else
    if [ ${bname: -3} == "tgz" ]; then
      format=".tgz"
    elif [ ${bname: -3} == "tbz" ]; then
      format=".tbz"
    elif [ ${bname: -6} == "tar.gz" ]; then
      format=".tar.gz"
    elif [ ${bname: -6} == "tar.bz" ]; then
      format=".tar.bz"
    elif [ ${bname: -7} == "tar.bz2" ]; then
      format=".tar.bz2"
    elif [ ${bname: -3} == "tar" ]; then
      format=".tar"
    fi
    elaborate1 $i
    tar -xf $file -C $path
    elaborate2
  fi
fi
done



#tar -xf $file -C $path
#tar -xf $file -C $path

#extension="${bname##*.}"
#filename="${bname%.*}"
#path=${zip%/*}
#extracted=${file%".zip"} #percorso file senza estensione
#${#array[@]} length of array
