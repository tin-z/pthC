#!/bin/bash

function fun_get_outputfile {
	cd "./input_outputs/" 
	ls output* | xargs
}


cond=$1
if [ $cond == 0 ]; then
	fun_get_outputfile
else
	printf "1"
fi
