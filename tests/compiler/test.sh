#!/bin/bash

# run cmm-compiler
function testCode {
    java -classpath $(dirname $0)/../../bin at.jku.ssw.cmm.compiler.CMM $1
}

# define different colors
red='\e[0;31m'
green='\e[0;32m'
yellow='\e[1;33m'
NC='\e[0m' # No Color

# count occouring failes
failes=0
tests=0
outputAll=0

if [ $# > 1 ];then
    if [ "$1" == "1" ];then
        outputAll=1
    fi
fi 

# Check code that is written to fail
echo "----------------------------------------------------------------"
echo "Check incorrect code"
echo "----------------------------------------------------------------"

for entry in "$(dirname $0)/failed"/*
do
    if [ -f "$entry" ];then
        tests=`expr $tests + 1`
        cmmOutput=$(testCode "$entry")

        if [ $? != 0 ];then
            printf "${green} %-80s ERROR DETECTED${NC}\n" $entry
            if [ $outputAll = 1 ];then
                echo -e "${yellow}$cmmOutput${NC}"
            fi
        else
            failes=`expr $failes + 1`
            printf "${red} %-80s ERROR NOT DETECTED${NC}\n" $entry
            echo -e "${yellow}$cmmOutput${NC}"
        fi 
    fi
done

# Check code that is written to success
echo "----------------------------------------------------------------"
echo "Check correct code"
echo "----------------------------------------------------------------"

for entry in "$(dirname $0)/success"/*
do
    if [ -f "$entry" ];then
        tests=`expr $tests + 1`
        cmmOutput=$(testCode "$entry")

        if [ $? != 0 ];then
            failes=`expr $failes + 1`
            printf "${red} %-80s ERROR DETECTED -> FAILED${NC}\n" $entry
            echo -e "${yellow}$cmmOutput${NC}"
        else
            printf "${green} %-80s NO ERROR${NC}\n" $entry
            if [ $outputAll = 1 ];then
                echo -e "${yellow}$cmmOutput${NC}"
            fi
        fi
    fi
done

if [ $failes == 0 ];then
    echo "----------------------------------------------------------------"
    echo -e "${green}all $tests Test(s) success!${NC}"
    echo "----------------------------------------------------------------"
    exit 0
else
    echo "----------------------------------------------------------------"
    echo -e "${red} $failes of $tests Test(s) failed!${NC}"
    echo "----------------------------------------------------------------"
    exit 2
fi
