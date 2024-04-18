#!/bin/bash

    if [ ! -f .target ]; then
        read -p ".target isn't existed."
        exit 1
    fi
    
    read target <.target
    if [ -z "$target" ]; then
        read -p ".target is empty."
        exit 1
    fi
    
    opts=()
    
    identity_file=
        shopt -s nullglob
        for id in ./.ssh/id_* ../.ssh/id_*; do
            identity_file="$id"
            break
        done
        if [ -n "$identity_file" ]; then
            opts+=(-e "ssh -i $identity_file")
        fi
        
    opts+=(
        -camv -L --delete \
        --exclude=/.ssh \
        --exclude=/classpath.lst \
        --exclude=/src \
        --exclude=/web \
        --exclude=/user-web \
        --exclude=/target/*.jar \
        )
    
    echo rsync "${opts[@]}" . "$target"
    rsync "${opts[@]}" . "$target"

