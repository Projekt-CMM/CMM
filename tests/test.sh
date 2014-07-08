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

# Check code that is written to fail
echo "----------------------------------------------------------------"
echo "Check incorrect code"
echo "----------------------------------------------------------------"

for entry in "failed"/*
do
    if [ -f "$entry" ];then
        cmmOutput=$(testCode "$entry")

        if [ $? != 0 ];then
            echo -e "${green}$entry \tERROR DETECTED${NC}"
        else
            failes=`expr $failes + 1`
            echo -e "${red}$entry \tERROR NOT DETECTED -> FAILED${NC}"
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
            echo -e "${red}$entry \tERROR DETECTED -> FAILED${NC}"
            echo -e "${yellow}$cmmOutput${NC}"
        else
            echo -e "${green}$entry \tNO ERROR${NC}"
        fi 
    fi
done

echo "----------------------------------------------------------------"
echo "$failes Test(s) failed"
