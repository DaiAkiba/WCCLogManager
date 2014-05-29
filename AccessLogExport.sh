#! /bin/bash
#
# Usage: 
#  AccessLogExport.sh [options]
#
# Options:
#   --start <yyyyMMdd>   # Set start date to export(required)
#   --end <yyyyMMdd>     # Set end date to export(required)
#   --order [DESC|ASC]   # Set order of data in export file(optional)
#                        # default: DESC
#
# Example:
#  AccessLogExport.sh --start 20140601 --end 20140630 --order ASC
#


function usage() {
    sed -rn '/^# Usage/,${/^#/!q;s/^# ?//;p}' "$0"
    exit 0
}

function removeSingleQuotation() {
  echo $1 | sed -e "s/'//g"
}

if [ "$#" -eq 0 ] ; then
    usage
fi

getopt -o ht: -l help,start:,end:,order: -- $* > /dev/null 2>&1
if [ "$?" -eq 1 ] ; then
    usage
fi


OPT=`getopt -o ht: -l help,start:,end:,order: -- $*`
set -- $OPT
while true
do
    case "$1" in
        -h | --help ) 
            usage;;
        --start ) 
            tmp=`removeSingleQuotation $2`;
            START=$tmp;
            shift 2;;
        --end )
            tmp=`removeSingleQuotation $2`;
            END=$tmp
            shift 2;;
        --order )
            tmp=`removeSingleQuotation $2`;
            ORDER=$tmp;
            shift 2;;
        *) break ;;
    esac
done

echo "START=${START}"
echo "END=${END}"
echo "ORDER=${ORDER}"
