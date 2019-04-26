#!/bin/bash
###sudo bash -c ' echo 0 > /proc/sys/kernel/perf_event_paranoid '
###[The default auxtrace mmap size for Intel PT is 4MiB/page_size for privileged users
###(or if /proc/sys/kernel/perf_event_paranoid < 0), 128KiB for unprivileged users.

cd "./record"
echo $1		
echo $2

text=$( objdump -h $2 | grep ".text" | sed -E 's\[ ]+\ \g' | xargs )
arr=( $text )

filter_address=$(echo ${arr[3]} | sed -E 's\^[0]+\\g' )
address_length=$(echo ${arr[2]} | sed -E 's\^[0]+\\g' )

base_address=$( cat /proc/$1/maps  | grep "$4" | grep "r-xp" | cut -d '-' -f 1 )
printf "$filter_address address are mapped in: $base_address with max size: $address_length\n\n"
cd "../"

if [ $1 -eq "0" ];then
	printf "$filter_address address are mapped in: $filter_address with max size: $address_length\n\n"
	eval "./scripts/memory_fine.sh $filter_address $filter_address $address_length"
else
	printf "$filter_address address are mapped in: $base_address with max size: $address_length\n\n"
	eval "./scripts/memory_fine.sh $base_address $filter_address $address_length"
fi
