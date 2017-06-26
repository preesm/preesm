#!/bin/bash 

#########
##	
## 	Automaticaly converts text files into UTF-8 format.
##	
## 	Ignores binaries if they match extensions in the list 
##	AUTHORIZED_BINARY_EXT. If a file has the type BINARY but not one
##	of the extensions in the list, it means it probably has a wrong 
##	type. Those file are reported, prefixed with "WRONGTYPED "
##	
##	File types that are not considered binaries and cannot be converted
##	are reported, prefixed with "UNSUPPORTED"
##	
## 	The reported files need manual handling.
##	
#########

echo ""

WINDOWS_CR_EXT=""
KEEP_EXCEPTIONS="HTM HTML"
AUTHORIZED_BINARY_EXT="JAR BMP JAR ICO XCF GIF PNG PDF IDB PDB LIB XLS RES A DOCX SUO NCB CLASS ZIP PPM JPG CCS SCH AI DOC XTENDBIN _TRACE XZ GZ "
AUTHORIZED_BINARY_ENC="BINARY APPLICATION/VND.MS-OFFICEBINARY APPLICATION/CDFV2-UNKNOWNBINARY APPLICATION/MSWORDBINARY"

WINDOWS_SPECIFIC_EXTENSIONS="BAT INF CMD SLN CSPROJ VBPROJ VCXPROJ VCPROJ DBPROJ FSPROJ LSPROJ WIXPROJ MODELPROJ SQLPROJ WMAPROJ USER XPROJ PROPS FILTERS VCXITEMS"


TMPLISTFILE=`mktemp --suffix=listfile`
TMPTEXTLISTFILE=`mktemp --suffix=textlistfile`
echo -n "" > $TMPTEXTLISTFILE
find * -type f > $TMPLISTFILE


echo " Processing charset"
echo ""

#
# iterate over all files, convert text files to UTF-8 and store their 
# path
#
while read -r line
do (
	TOTALCOUNT=$[$TOTALCOUNT +1]
    file="$line"
    BASENAME=$(basename "$file")
    EXTENSION="${BASENAME##*.}"
    EXTENSION=`echo $EXTENSION | tr [a-z] [A-Z]`
    #echo $EXTENSION
	#echo $file
	#echo -n "  "
	ENCODING=`file --mime-encoding "$file" | rev | cut -d' ' -f1 | rev | tr [a-z] [A-Z]`
	if [ "$ENCODING" == "UTF-8" -o "$ENCODING" == "US-ASCII" ]; then
		TEXTCOUNT=$[$TEXTCOUNT +1]
		UTF8COUNT=$[$UTF8COUNT +1]
		echo "$file" >> $TMPTEXTLISTFILE
	else
		SUPPORTEDBINARY=0
		for ENC in $AUTHORIZED_BINARY_ENC; do
			if [ "$ENC" == "$ENCODING" ]; then
				SUPPORTEDBINARY=1
				break
			fi
		done
		
		KEEP=0
		for KEEPEXT in $KEEP_EXCEPTIONS; do
			if [ "$KEEPEXT" == "$EXTENSION" ]; then
				KEEP=1
				break
			fi
		done
		
		if [ "$SUPPORTEDBINARY" == "0" -a "$KEEP" == "0" ]; then
			#echo "conv [$ENCODING] $file"
			TMPFILE2=`mktemp`
			cp "$file" $TMPFILE2
			iconv -f $ENCODING -t UTF8 $TMPFILE2 -o "$file" 2> /dev/null
			RES=$?
			if [ "$RES" != "0" ]; then
				echo "UNSUPPORTED [$ENCODING] $file"
				cp $TMPFILE2 "$file"
				UNSUPPORTEDCOUNT=$[$UNSUPPORTEDCOUNT +1]
				BINARYCOUNT=$[$BINARYCOUNT +1]
			else 
				echo "[Convert from $ENCODING to UTF-8] $file"
				CONVERTEDCOUNT=$[$CONVERTEDCOUNT +1]
				TEXTCOUNT=$[$TEXTCOUNT +1]
				echo "$file" >> $TMPTEXTLISTFILE
			fi
			rm $TMPFILE2
		else
			AUTHEXT=0
			for EXT in $AUTHORIZED_BINARY_EXT; do
				if [ "$EXT" == "$EXTENSION" ]; then
					AUTHEXT=1
					break
				fi
			done
			CONTENT=`cat "$file"`
			if [ "$AUTHEXT" == "0" -a "$CONTENT" != "" ]; then
				echo "POTENTIALY WRONGTYPED [$ENCODING] $file"
				WRONGTYPEDCOUNT=$[$WRONGTYPEDCOUNT +1]
			fi
			BINARYCOUNT=$[$BINARYCOUNT +1]
		fi
	fi
) & done < $TMPLISTFILE
wait
rm $TMPLISTFILE

echo ""
echo " Processing line endings"
echo ""

#
# iterate over all text files and fix line ending (except for 
# windows files)
#
while read -r line
do (
	file="$line"
    BASENAME=$(basename "$file")
    EXTENSION="${BASENAME##*.}"
    EXTENSION=`echo $EXTENSION | tr [a-z] [A-Z]`
    
	WINDOWSSPEC=0
	for WINEXT in $WINDOWS_SPECIFIC_EXTENSIONS; do
		if [ "$WINEXT" == "$EXTENSION" ]; then
			WINDOWSSPEC=1
			break
		fi
	done
	
	TMPFILE3=`mktemp --suffix=tmp3CRLF` 
	cp "$file" $TMPFILE3
	
	if [ "$WINDOWSSPEC" == "1" ]; then
		cp "$TMPFILE3" "$file"
	else
		cat $TMPFILE3 | sed -r 's/\r(\n)?$//g' > "$file"
	fi
	rm $TMPFILE3
) & done < $TMPTEXTLISTFILE
wait
rm $TMPTEXTLISTFILE

