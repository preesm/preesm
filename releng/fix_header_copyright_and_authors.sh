#!/bin/bash -eu

echo ""

#########
##
##  Automatically replace dates and author list
##  in files containing the corresponding patterns
##  (see {LOWDATE|UPPDATE|AUTHORS}PATTERN variable
##  below). Information is fetched from the git 
##  repository.
##
##  Note: this script should be used after a pass of
##  https://wiki.eclipse.org/Development_Resources/How_to_Use_Eclipse_Copyright_Tool
##  with the correct header (see http://www.cecill.info/placer.en.html) 
##  with patterns (see copyright_template.txt file) and 
##  applying back the UTF-8 encoding.
##
#########

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

cd $DIR

mvn -P releng -Dtycho.mode=maven license:format

function fixFile {
    file=$1

    BASENAME=$(basename "$file")
    EXTENSION="${BASENAME##*.}"
    EXTENSION=`echo $EXTENSION | tr [a-z] [A-Z]`
    
	case $EXTENSION in
		BAT)
			#"@rem "
			COMMENT="@rem "
			;;
		C |	CPP | H | JAVA | XTEND | BSH)
			#" * "
			COMMENT=" * "
			;;
		MAK | PROPERTIES)
			#"# "
			COMMENT="# "
			;;
		XML | HTML | ECORE | GENMODEL)
			#"    "
			COMMENT="    "
			;;
		*)
			#echo "Unsupported file extension $EXTENSION"
			;;
	esac
	
    FILEAUTHORLIST=`git log --follow --use-mailmap --date=format:'%Y' --format='%aE' "$file" | sort -u`
    for AUTHOR in $FILEAUTHORLIST; do 
		AUTHORUPPERDATE=`git log --follow --use-mailmap --date=format:'%Y' --format='%ad %aE' "$file" | sort -u | grep $AUTHOR | tail -n 1 | cut -d' ' -f1`
		AUTHORLOWERDATE=`git log --follow --use-mailmap --date=format:'%Y' --format='%ad %aE' "$file" | sort -u | grep $AUTHOR | head -n 1 | cut -d' ' -f1`
		if [ "$AUTHORLOWERDATE" == "$AUTHORUPPERDATE" ]; then
			AUTHORDATE="($AUTHORLOWERDATE)"
		else
			AUTHORDATE="($AUTHORLOWERDATE - $AUTHORUPPERDATE)"
		fi
		
		LINE=`git log --follow --use-mailmap --date=format:'%Y' --format='%aN <%aE>' "$file" | sort -u | grep $AUTHOR`
		sed -i -e "s/$AUTHORSPATTERN/${LINE} ${AUTHORDATE}\n$COMMENT$AUTHORSPATTERN/g" "$file"
    done
    
	TMPFILE2=`mktemp --suffix=tosed`
	cat "$file" | grep -v "$AUTHORSPATTERN" > $TMPFILE2
	
	
    LOWDATE=`git log --follow --date=format:'%Y' --format='%ad' "$file" | sort -u | head -n 1`
    UPPDATE=`git log --follow --date=format:'%Y' --format='%ad' "$file" | sort -u | tail -n 1`
	
	if [ "$LOWDATE" == "$UPPDATE" ]; then
		GLOBDATE="$LOWDATE"
	else
		GLOBDATE="$LOWDATE - $UPPDATE"
	fi
		
    cat "$TMPFILE2" | sed -e "s/$DATEPATTERN/$GLOBDATE/g" > "$file" 
    rm $TMPFILE2
}

DATEPATTERN="%%DATE%%"
AUTHORSPATTERN="%%AUTHORS%%"

TMPFILE=`mktemp --suffix=biglisttosed`
grep "%%AUTHORS%%" -R | cut -d':' -f1 | sort -u | grep -v "copyright_template.txt" | grep -v "fix_header_copyright_and_authors.sh" | grep -v "VAADER_eclipse_preferences.epf" | grep -v "README" > $TMPFILE

NBFILES=`cat $TMPFILE | wc -l`

echo " Starting (#files = $NBFILES)"

time (
NBCPUS=`grep -c ^processor /proc/cpuinfo`
((NBTHREADS=NBCPUS*2))

while read -r line
do
  MOD=$((NBFILES % NBTHREADS))
  [ $MOD -eq 0 ] && echo "    $NBFILES left ..." && wait
  NBFILES=$((NBFILES-1))
  fixFile "$line" &
done < $TMPFILE
wait
)
rm $TMPFILE

echo " Done."
echo ""
