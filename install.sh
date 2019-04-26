#!/bin/bash

DIR_install="./src"

fsc "$DIR_install/block_code.scala"
fsc "$DIR_install/function_code.scala"
fsc "$DIR_install/edges.scala"
fsc "$DIR_install/ParseU.scala"
fsc "$DIR_install/Parse_ELF.scala"
fsc "$DIR_install/Archit_parser_Linux.scala"
fsc "$DIR_install/Archit_parser.scala"

fsc main.scala

fsc -shutdown
