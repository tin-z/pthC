#!/bin/bash
ps | grep $1 | xargs | cut -d ' ' -f 1 | head -1 | xargs
