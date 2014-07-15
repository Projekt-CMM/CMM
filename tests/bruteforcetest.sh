#!/bin/bash

# run cmm-compiler
function testCode {
    java -classpath ../bin at.jku.ssw.cmm.compiler.CMM $1
}

function generateWord {
    # generate random-number from 0-44
    r=$(( $RANDOM %45 ));
    # get word
    case "$r" in
        0)  printf "void" ;;
        1)  printf "char" ;;
        2)  printf "int" ;;
        3)  printf "float" ;;
        4)  printf "main(" ;;
        5)  printf "foo(" ;;
        6)  printf "(" ;;
        7)  printf "(" ;;
        8)  printf "(" ;;
        9)  printf ")" ;;
        10) printf ")" ;;
        11) printf ")" ;;
        12) printf "x" ;;
        13) printf "y" ;;
        14) printf "z" ;;
        15) printf $(( $RANDOM % 256 )); ;; # Print any character
        16) printf "=" ;;
        17) printf "==" ;;
        18) printf "+" ;;
        19) printf "-" ;;
        20) printf "if(" ;;
        21) printf "else" ;;
        22) printf "while(" ;;
        23) printf "const" ;;
        24) printf "void main(){" ;;
        25) printf "{";;
        26) printf "{";;
        27) printf "{";;
        28) printf "{";;
        29) printf "}";;
        30) printf "}";;
        31) printf "}";;
        32) printf "}";;
        33) printf "[";;
        34) printf "]";;
        35) printf ".";;
        36) printf "Struct";;
        34) printf "ref";;
        35) printf "++";;
        36) printf "--";;
        36) printf "ä";;
        37) printf "string";;
        *) printf ";" ;;
    esac
}

function generateCode {
    while [ 1 != $(( $RANDOM %40 )) ] 
    do
        printf $(generateWord)
        printf " "
    done
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

printf "${yellow}Start Bruteforce-Test${NC}\n\n"

while [ true ]
do
    date=$(date)
    # write generated code into bruteforce.c
    code=$(generateCode)
    echo $code > bruteforce.c

    # test code
    cmmOutput=$(testCode "bruteforce.c")

    cmmstate=$?

    if [ $cmmstate == 0 ];then
        echo "----------------------------------------------------------------"
        printf "${yellow} $code \n ${green}NO ERRORS${NC}\n"
        echo $cmmOutput

        # save file
        mkdir -p bruteforce_success
        echo 
       cp bruteforce.c "bruteforce_success/$date.c"
    elif [ $cmmstate == 2 ]; then
        if [ $outputAll == 1 ]; then
            printf "${yellow} $code \n ${green}ERROR FOUND${NC}\n"
        fi
    else
        echo "----------------------------------------------------------------"
        printf "${yellow} $code \n ${red}FAILED${NC}\n"
        echo $cmmOutput

        # save file
        mkdir -p bruteforce_failed
        cp bruteforce.c "bruteforce_failed/$date.c"
    fi

done

echo "----------------------------------------------------------------"
echo "$failes Test(s) failed"
