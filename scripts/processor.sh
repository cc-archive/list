#!/bin/bash                                                                                                                                                                                                                                                                               

for f in *.jpg
do
    if [ ! -f 200/$f ]
    then
        if [[ $(cc-xmp-tag --read $f ALL) == "" ]]
        then
            cc-xmp-tag --write $f License='BY'
        fi
        #convert -define jpeg:size=200x200 $f -thumbnail 200x200^ -gravity center -extent 200x200 200/$f

	
        convert $f -thumbnail 200x200^ -auto-orient -gravity center -extent 200x200 200/$f
        for n in 300 400 600 800 1024
        do
	    mkdir -p $n
            if [ ! -f $n/$f ]
                then
                convert $f -auto-orient -resize $n $n/$f
            fi
        done
    fi

done
