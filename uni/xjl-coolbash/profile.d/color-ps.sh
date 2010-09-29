PS1="\[\e]0;\u@\h: \w\a\]\${debian_chroot:+(\$debian_chroot)}"
PS1="$PS1\[\e[1;42m\]\u@\h:\[\e[0;47m\]\w\[\e[42m\]\$ \[\e[m\]"
export PS1
