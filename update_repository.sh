#!/bin/bash
#
# rebase own git-fork with main git repository
#
# fork-repository is defined as origin

main_url="https://github.com/Projekt-CMM/CMM.git"

git pull --rebase $main master
git push origin master
