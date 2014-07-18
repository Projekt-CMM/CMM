#!/bin/bash
#
# rebase own git-fork with main git repository
#
# fork-repository is defined as origin

git pull --rebase https://github.com/Projekt-CMM/CMM.git master
git push origin master
