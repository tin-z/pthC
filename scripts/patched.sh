#!/bin/bash

#E.g. value's env variable

program_name=$1
#"bash_4.4"

repository_default=$2
#security.ubuntu.com/ubuntu/pool/main/b/bash

orig=$3
#bash_4.4.orig.tar.xz

patch1=$4
#bash_4.4-2ubuntu1.debian.tar.xz

patch2=$5
#bash_4.4-2ubuntu1.1.debian.tar.xz

dsc1=$6
#bash_4.4-2ubuntu1.dsc

dsc2=$7
#bash_4.4-2ubuntu1.1.dsc

name=$8
#bash

from="1"
to="2"

to_download=(
	$orig
	$patch1
	$dsc1
	$patch2
	$dsc2
)

dir_dest="./softbin/$program_name"
mkdir -p $dir_dest
cd $dir_dest

for i in ${to_download[@]}; do
	wget "$repository_default/$i" -O $i ;
done

rm -r $from
rm -r $to
dpkg-source -x $dsc1 $from
dpkg-source -x $dsc2 $to


cd $from

./configure
./config
make

cp $name "../$name$from"
diff1=$( cat "debian/patches/series" | sed -e 's/#//g' )

cd "../$to"

./configure
./config
make

cp $name "../$name$to"
diff2=$( cat "debian/patches/series" | sed -e 's/#//g')


#every (most of ) deb package has debian/patch/series
set -f
array1=(${diff1//[ ]+/})
array2=(${diff2//[ ]+/})

echo ${array1[@]}
echo
echo ${array2[@]}
echo
diff3=$( echo $diff1 $diff2 |tr '[ ]+' '\n' | sort | uniq -u )
array3=(${diff3//[ ]+/})

echo "Functions: differing are:"
#echo $array3
#echo ${array3[@]}
#the file diff or patch wich make the two version differ.

cd ..
return_FF=""

for i in ${array3[@]}; do
echo "file: $i"
tmp=($( cat "$to/debian/patches/$i" | grep "@@" | sed 's/@@.*\(@@.*\)/\1/' | uniq | sed 's/@@//g' | xargs ))
	for i in ${tmp[@]}; do
		return_FF="$return_FF <$i>"
	done
done

cd "../../input_outputs/"
echo $return_FF | xargs > function_list.txt

cd "../"
printf "Done. now from ./ launch this command\n"
printf "scala TakeCare -p $PWD/softbin/$program_name/$name$to -c $name$to -f ./input_outputs/function_list.txt \n"

