#!/bin/bash

alias d='ls -FANl --color=auto'
alias dt='d -t | tail'
alias ps='COLUMNS=1000 ps fx'

function hello() {
	echo Hello, this is Lenik.
}

export EDITOR=vi
export PATH=$PATH:~/bin:~/sbin


alias up='svn update --ignore-externals'
