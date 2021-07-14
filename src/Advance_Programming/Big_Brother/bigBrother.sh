#!/bin/bash

#get path
cd $1

if [ ! -e "fileList.txt" ]
then
	#firt time
	echo "Welcome to the Big Brother"
	ls -I fileList.txt > fileList.txt
else
	#get old files
	let index=0
	while read line
	do
		oldFiles[$index]=$line
		index=$(( index + 1 ))
	done < fileList.txt

	ls -I tempList.txt > tempList.txt
	let index=0
	while read line
	do
		currentFiles[$index]=$line
		index=$(( index + 1 ))
	done < tempList.txt

	#check deleted
	for oldFile in "${oldFiles[@]}"
	do
		let found=0
		for currentFile in "${currentFiles[@]}"
		do
			if [ "$oldFile" == "$currentFile" ]
			then
				found=1
			fi
		done

		if [ $found -eq 0 ]
		then
			echo "File deleted: $oldFile"		
		fi
	done

	#check new
	for currentFile in "${currentFiles[@]}"
	do
		let found=0
		for oldFile in "${oldFiles[@]}"
		do
			if [ "$oldFile" == "$currentFile" ] || [ "$currentFile" == "fileList.txt" ]
			then
				found=1
			fi
		done

		if [ $found -eq 0 ] && [ -f "$currentFile" ] ; then
			echo "File created: $currentFile"
		elif [ $found -eq 0 ] && [ -d "$currentFile" ] ; then
			echo "Folder created: $currentFile"
		fi
	done

	#update script files
	rm tempList.txt
	ls -I fileList.txt > fileList.txt
fi
	
