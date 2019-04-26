#!/bin/bash
cat /proc/$1/maps  | grep "$2" | grep "r-xp" | cut -d '-' -f 1 | xargs
