#!/bin/bash

alias d='ls -FANl --color=auto'
alias dt='d -t | tail'

function hello() {
	echo Hello, this is Lenik.
}

export EDITOR=vi
export PATH=$PATH:~/bin:~/sbin
