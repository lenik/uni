
alias cls="cmd /c cls"
alias l='ls -FANo --color'
alias du='du --exclude=.svn'
alias tree='d -T | less'

EMACSD=/lapiota/abc.d/e/emacs-21.0.104
export EMACSLOADPATH=/usr/share/emacs/21.2/lisp:$EMACSD/lisp:$EMACSD/site-lisp

#. /lapiota/lib/sh/initdir
#initdir /lapiota

function xmlspy() {
    cmd /c start xmlspy `cygpath -aw "$1"`
}
