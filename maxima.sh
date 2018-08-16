#!/bin/bash
echo $1
maxima --very-quiet -r $1 # pass the bash argument into maxima