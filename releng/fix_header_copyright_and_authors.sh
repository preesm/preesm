#!/bin/bash

#########
##	
## 	Automatically replace dates and author list
##  in files containing the corresponding patterns
##  (see {LOWDATE|UPPDATE|AUTHORS}PATTERN variable
##  below). Information is fetched from the git 
##  repository.
##  
##  Note: this script should be used after a pass of
##  https://wiki.eclipse.org/Development_Resources/How_to_Use_Eclipse_Copyright_Tool
##  and applying back the UTF-8 encoding.
##	
#########


#EXTENSIONLIST=`git status --porcelain | rev | cut -d"." -f1 | rev | sort -u | tr [a-z] [A-Z]`
#echo $EXTENSIONLIST
#git status --porcelain | wc -l
#exit

LOWDATEPATTERN="%%LOWERDATE%%"
UPPDATEPATTERN="%%UPPERDATE%%"
AUTHORSPATTERN="%%AUTHORS%%"

TMPFILE=`mktemp --suffix=biglisttosed`
grep "%%AUTHORS%%" -R | cut -d':' -f1 | sort -u > $TMPFILE

echo ""
echo " Starting" 

while read -r line
do (
	file="$line"
    BASENAME=$(basename "$file")
    EXTENSION="${BASENAME##*.}"
    EXTENSION=`echo $EXTENSION | tr [a-z] [A-Z]`
    
	case $EXTENSION in
		BAT)
			#"@rem "
			COMMENT="@rem "
			;;
		C |	CPP | H | JAVA)
			#" * "
			COMMENT=" * "
			;;
		MAK | PROPERTIES)
			#"# "
			COMMENT="# "
			;;
		XML)
			#"    "
			COMMENT="    "
			;;
		*)
			#echo "Unsupported file extension $EXTENSION"
			;;
	esac
	
    FILEAUTHORLIST=`git log --date=format:'%Y' --format='%aE' "$file" | sort -u`
    #echo $file
    for AUTHOR in $FILEAUTHORLIST; do 
		AUTHORUPPERDATE=`git log --use-mailmap --date=format:'%Y' --format='%ad' --author=$AUTHOR "$file" | sort -u | tail -n 1`
		AUTHORLOWERDATE=`git log --use-mailmap --date=format:'%Y' --format='%ad' --author=$AUTHOR "$file" | sort -u | head -n 1`
		if [ "$AUTHORLOWERDATE" == "$AUTHORUPPERDATE" ]; then
			AUTHORDATE="($AUTHORLOWERDATE)"
		else
			AUTHORDATE="($AUTHORLOWERDATE - $AUTHORUPPERDATE)"
		fi
		
		LINE=`git log --use-mailmap --author=$AUTHOR --date=format:'%Y' --format='%aN <%aE>' "$file" | sort -u | sed -r "s/$/ $AUTHORDATE/g"`
		#echo "$LINE"
		#perl -i -0777 -pe "s/$AUTHORSPATTERN/${LINE}\n$COMMENT$AUTHORSPATTERN/g" "$file"
		sed -i -e "s/$AUTHORSPATTERN/${LINE}\n$COMMENT$AUTHORSPATTERN/g" "$file"
    done
    
	TMPFILE2=`mktemp --suffix=tosed`
	cat "$file" | grep -v "$AUTHORSPATTERN" > $TMPFILE2
	
	
    LOWDATE=`git log --date=format:'%Y' --format='%ad' "$file" | sort -u | head -n 1`
    UPPDATE=`git log --date=format:'%Y' --format='%ad' "$file" | sort -u | tail -n 1`
	
    cat "$TMPFILE2" | \
    sed -e "s/$LOWDATEPATTERN/$LOWDATE/g" | \
    sed -e "s/$UPPDATEPATTERN/$UPPDATE/g" > "$file" 
    rm $TMPFILE2
) & done < $TMPFILE

sleep 2
echo " Waiting for the threads to finish ..."

wait
rm $TMPFILE

echo " Done."
echo ""
