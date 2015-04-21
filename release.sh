#!/bin/sh

printf "Enter version of C Compact  -> "

read VERSION

mkdir "../C_Compact_Alpha_$VERSION"

while read LINE           
do           
    echo $LINE
	cp -r "$LINE" "../C_Compact_Alpha_$VERSION/"
done < "./release.txt" 
