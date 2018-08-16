#!/bin/bash
maxima --very-quiet -r "$1" # pass the bash argument into maxima
echo "This is the passed argument $1"