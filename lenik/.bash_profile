
alias cls="cmd /c cls"
alias l='ls -FANo --color'
alias du='du --exclude=.svn'
alias tree='d -T | less'

export PATH=~/bin:~/sbin:$PATH

EMACSD=/lapiota/abc.d/e/emacs-21.0.104
export EMACSLOADPATH=/usr/share/emacs/21.2/lisp:$EMACSD/lisp:$EMACSD/site-lisp

export EDITOR=vi

#. /lapiota/lib/sh/initdir
#initdir /lapiota

function xmlspy() {
    cmd /c start xmlspy `cygpath -aw "$1"`
}
