#!/bin/bash

cd "./input_outputs"
file="config_memory.txt"
condition="y"

base_address=$( echo $2 | sed 's/[0]*\(.*\)/\1/' )
filter_address=$( echo $1 | sed 's/[0]*\(.*\)/\1/' )


if [ ${#base_address} -eq ${#filter_address} ]; then
	if [ $( echo $base_address | sed 's/\([a-zA-Z0-9]\).*/\1/' ) -eq $( echo $filter_address | sed 's/\([a-zA-Z0-9]\).*/\1/' ) ] ; then
		condition="n"
	fi
fi

echo "$filter_address $base_address $3 $condition" > $file
chmod ugo+rw $file
