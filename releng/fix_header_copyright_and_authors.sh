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
	
	FILEAUTHORLISTWITHDATES=`git log --follow --use-mailmap --date=format:'%Y' --format='%ad %aN <%aE>' "$file" | sort -u`
    FILEAUTHORLIST=`echo "$FILEAUTHORLISTWITHDATES" | rev | cut -d' ' -f1 | rev | sort -u`
    for AUTHOR in $FILEAUTHORLIST; do
		AUTHORDATELIST=`echo "$FILEAUTHORLISTWITHDATES" | grep "$AUTHOR" | cut -d' ' -f1 | sort -u`
		AUTHORUPPERDATE=`echo "$AUTHORDATELIST" | tail -n 1`
		AUTHORLOWERDATE=`echo "$AUTHORDATELIST" | head -n 1`
		if [ "$AUTHORLOWERDATE" == "$AUTHORUPPERDATE" ]; then
			AUTHORDATE="($AUTHORLOWERDATE)"
		else
			AUTHORDATE="($AUTHORLOWERDATE - $AUTHORUPPERDATE)"
		fi
		
		LINE=`echo "$FILEAUTHORLISTWITHDATES" | grep "$AUTHOR" | cut -d' ' -f2- | sort -u`
		sed -i -e "s/$AUTHORSPATTERN/${LINE} ${AUTHORDATE}\n$COMMENT$AUTHORSPATTERN/g" "$file"
    done
    
	TMPFILE2=`mktemp --suffix=tosed`
	cat "$file" | grep -v "$AUTHORSPATTERN" > $TMPFILE2
	
	DATELIST=`echo "$FILEAUTHORLISTWITHDATES" | cut -d' ' -f1 | sort -u`
    LOWDATE=`echo "$DATELIST" | head -n 1`
    UPPDATE=`echo "$DATELIST" | tail -n 1`
	
	if [ "$LOWDATE" == "$UPPDATE" ]; then
		GLOBDATE="$LOWDATE"
	else
		GLOBDATE="$LOWDATE - $UPPDATE"
	fi
		
    cat "$TMPFILE2" | sed -e "s/$DATEPATTERN/$GLOBDATE/g" > "$file" 
    rm $TMPFILE2
}

# from https://github.com/fearside/ProgressBar/
function ProgressBar {
    let _progress=(${1}*100/${2}*100)/100
    let _done=(${_progress}*5)/10
    let _left=50-$_done
    _fill=$(printf "%${_done}s")
    _empty=$(printf "%${_left}s")
	printf "\rProgress : [${_fill// /#}${_empty// /-}] ${_progress}%%"
}

DATEPATTERN="%%DATE%%"
AUTHORSPATTERN="%%AUTHORS%%"

TMPFILE=`mktemp --suffix=biglisttosed`
grep "%%AUTHORS%%" -R | cut -d':' -f1 | sort -u | grep -v "copyright_template.txt" | grep -v "fix_header_copyright_and_authors.sh" | grep -v "VAADER_eclipse_preferences.epf" | grep -v "README" > $TMPFILE

echo ""
echo " Header template applied."
echo " Replacing author and date patterns using git log data..."
echo ""

NBFILES=`cat $TMPFILE | wc -l`
NBFILESPROCESSED=0
time (
NBCPUS=`grep -c ^processor /proc/cpuinfo`
((NBTHREADS=NBCPUS*2))
while read -r line
do
  MOD=$((NBFILESPROCESSED % NBTHREADS))
  [ $MOD -eq 0 ] && ProgressBar ${NBFILESPROCESSED} ${NBFILES} && wait
  NBFILESPROCESSED=$((NBFILESPROCESSED+1))
  fixFile "$line" &
done < $TMPFILE
ProgressBar $((NBFILES-1)) ${NBFILES}
echo " Done."
wait
)
rm $TMPFILE

echo ""
