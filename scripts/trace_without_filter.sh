#!/bin/bash
###sudo bash -c ' echo 0 > /proc/sys/kernel/perf_event_paranoid '
###[The default auxtrace mmap size for Intel PT is 4MiB/page_size for privileged users
###(or if /proc/sys/kernel/perf_event_paranoid < 0), 128KiB for unprivileged users.


cd "./record"
rm -f *
echo $1
echo $2
echo $3
echo $4
text=$( objdump -h $4 | grep ".text" | sed -E 's\[ ]+\ \g' | xargs )
arr=( $text )

filter_address=$(echo ${arr[3]} | sed -E 's\^[0]+\\g' )
address_length=$(echo ${arr[2]} | sed -E 's\^[0]+\\g' )

base_address=$( cat /proc/$1/maps  | grep "$4" | grep "r-xp" | cut -d '-' -f 1 )

printf "$filter_address address are mapped in: $base_address with max size: $address_length\n\n"


#FIRST take the base address. so watch what type of elf is and rework the string outputted from perf script
echo "
perf record -m,$2 --pid $1 -e intel_pt// -a --switch-output=$3"

perf record -m,$2 --pid $1 -e intel_pt// -a --switch-output=$3
###########
#perf record -m,$2 --pid $1 -e intel_pt// 
#esempio di /usr/lib/firefox
#perf record -m,16384 --pid 6649 -e intel_pt// -a --filter 'filter 0x5bd0 /0x20cf2 @/usr/lib/firefox/firefox' sleep 6
#need to work this
#perf record -m,16384 --pid 6649 -e intel_pt// -a --filter 'filter 0x5bd0 /0x20cf2 @/usr/lib/firefox/firefox' --switch-output=60M
#but need to recompile kernel
#sudo perf record -m,2^n --pid $1 -e intel_pt// 
#example 2^14 * 4k = 64 mb
###########


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
eval "./scripts/memory_fine.sh $filter_address $base_address $address_length"
printf "All stuff has done here\n"
