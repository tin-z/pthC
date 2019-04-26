#!/bin/bash

cd ./record
chmod ugo+rw *

cd "./../input_outputs"
rm -f output*

list_perf_datas=($( ls ../record | xargs ))
cnt=1
for i in ${list_perf_datas[@]}
do
	#(e.g. file -Fip,dso,addr 1G vs file -Fip,addr 500M) togliamo dso
	#(old command perf script -Fip,addr -i ../record/$i | sed 1,1d )
	perf script -Fip,addr -i ../record/$i > "output$cnt.txt"
	return=$?
	if [ $return != 0 ]; then
		printf "$i is unrecognizable (sometimes is normal for the last perf.data.xxx) \n"
		eval "rm output$cnt.txt"
	else
		cnt=$(($cnt + 1))
	fi
done
chmod ugo+rw output*
cd "../"

printf "All stuff has done here\n"
