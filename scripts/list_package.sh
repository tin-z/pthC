#!/bin/bash

#### Tested with ubuntu-16-04 (apt)


#E.g. value's env variable

command=$1	
#bash

repository_Packages=$( apt-cache showpkg $command | grep lists/security | xargs | cut -f3 -d ' ' | sed 's/.*\/lists\/\(.*\)).*/\1/' | sed 's/_/\//g' )
#echo $repository_Packages
#security.ubuntu.com/ubuntu/dists/xenial-security/main/binary-amd64/Package

repo=$( echo $repository_Packages | sed 's/\(.*ubuntu\/\).*/\1/' )	
#echo $repo
#security.ubuntu.com/ubuntu/

repository_Packages="$repository_Packages.gz"
#echo $repository_Packages
#security.ubuntu.com/ubuntu/dists/xenial-security/main/binary-amd64/Packages.gz

cd './softbin'
wget $repository_Packages	#now we have on ./ the Packages.gz
gzip -d 'Packages.gz'		#gzip will delete the file.gz

version_specific=$( cat "Packages" | grep -E "Package: $command$" -A 25 | grep -E "Filename.*$command.*" | xargs | cut -f2 -d ' '| sed 's/\(.*ubuntu\).*/\1/' )
#echo $version_specific
#pool/main/b/bash/bash_4.3-14ubuntu

version_orig=$( echo $version_specific | sed 's/\(.*\)-[0-9]*.*/\1/' )
#echo $version_orig
#pool/main/b/bash/bash_4.3

dir=$( echo $version_orig | sed 's/\(.*\/\).*/\1/' )
#echo $dir
#pool/main/b/bash/

file_original="$version_orig.orig.tar.xz"
#echo $file_original
#pool/main/b/bash/bash_4.3.orig.tar.xz

version_orig2=$( echo $version_orig | sed "s/.*\($command.*\)/\1/" )
#echo $version_orig2
#bash_4.3

wget $repo$dir -O "index.html"
orig=$( cat "index.html" | grep $command | sed "s/.*\(.*$command.*\)<\/a>.*/\1/" | sed "s/[ ]+/\n/g" | grep "orig" | xargs)
echo "select the version (without .extension, e.g if u want bash_4.3.orig.tar.xz type bash_4.3)"
echo $orig
echo 
echo -n "# "
read -r typeZ
orig=$( cat "index.html" | grep $typeZ | sed "s/.*\(.*$typeZ.*\)<\/a>.*/\1/" | sed "s/[ ]+/\n/g" | grep "orig" | xargs)
array=($orig)

orig=${array[0]}
echo "version selected: $orig"

MACHINE_TYPE=$( uname -m )

if [ $MACHINE_TYPE == 'x86_64' ]; then
	to_download=($( cat "index.html" | grep $typeZ | sed "s/.*\(.*$typeZ.*\)<\/a>.*/\1/" | sed "s/[ ]+/\n/g" | grep -v "orig" | grep -v "i386" | xargs))
else
	to_download=($( cat "index.html" | grep $typeZ | sed "s/.*\(.*$typeZ.*\)<\/a>.*/\1/" | sed "s/[ ]+/\n/g" | grep -v "orig" | grep -v "amd64" | xargs))
fi
printf "\n#\n#\n#Choose the versions from and to diff:\n"

j=0

for i in ${to_download[@]}
do
	printf "$j - $i\n"
	j=$(($j + 1))
done


printf "\n#From version ( .debian.tar.gz ): "
read -r fromZ
dsc1=$( echo ${to_download[$fromZ]} | sed "s/\(.*\)\.debian\.tar\.gz.*/\1/")
#echo $dsc1
dsctemp=$( echo ${to_download[$fromZ]} | sed "s/\(.*\)\.debian\.tar\.xz.*/\1/")
#echo $dsctemp
if [ ${#dsctemp} -ge 1 ]; then
	if [ ${#dsc1} -ge ${#dsctemp} ]; then
		dsc1=$dsctemp
	fi
fi
dsc1="$dsc1.dsc"

printf "\n#to version ( .debian.tar.gz ): "
read -r toZ
dsc2=$( echo ${to_download[$toZ]} | sed "s/\(.*\)\.debian\.tar\.gz.*/\1/")
#echo $dsc2
dsctemp=$( echo ${to_download[$toZ]} | sed "s/\(.*\)\.debian\.tar\.xz.*/\1/")
#echo $dsctemp
if [ ${#dsctemp} -ge 1 ]; then
	if [ ${#dsc2} -ge ${#dsctemp} ]; then
		dsc2=$dsctemp
	fi
fi
dsc2="$dsc2.dsc"

cd "./../"

#echo "$typeZ $repo$dir $orig ${to_download[fromZ]} ${to_download[toZ]} $dsc1 $dsc2 $command"
eval "./scripts/patched.sh $typeZ $repo$dir $orig ${to_download[fromZ]} ${to_download[toZ]} $dsc1 $dsc2 $command"
