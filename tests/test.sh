#!/bin/bash

# run cmm-compiler
function testCode {
    java -classpath ../bin cmm.compiler.CMM $1
}

# define different colors
red='\e[0;31m'
green='\e[0;32m'
yellow='\e[1;33m'
NC='\e[0m' # No Color

# count occouring failes
failes=0
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

for entry in "failed"/*
do
    if [ -f "$entry" ];then
        cmmOutput=$(testCode "$entry")

        if [ $? != 0 ];then
            printf "${green} %-70s ERROR DETECTED${NC}\n" $entry
            if [ $outputAll = 1 ];then
                echo -e "${yellow}$cmmOutput${NC}"
            fi
        else
            failes=`expr $failes + 1`
            printf "${red} %-70s ERROR NOT DETECTED${NC}\n" $entry
            echo -e "${yellow}$cmmOutput${NC}"
        fi 
    fi
done

# Check code that is written to success
echo "----------------------------------------------------------------"
echo "Check correct code"
echo "----------------------------------------------------------------"

for entry in "success"/*
do
    if [ -f "$entry" ];then
        cmmOutput=$(testCode "$entry")

        if [ $? != 0 ];then
            failes=`expr $failes + 1`
            printf "${red} %-70s ERROR DETECTED -> FAILED${NC}\n" $entry
            echo -e "${yellow}$cmmOutput${NC}"
        else
            printf "${green} %-70s NO ERROR${NC}\n" $entry
            if [ $outputAll = 1 ];then
                echo -e "${yellow}$cmmOutput${NC}"
            fi
        fi 
    fi
done

echo "----------------------------------------------------------------"
echo "$failes Test(s) failed"
