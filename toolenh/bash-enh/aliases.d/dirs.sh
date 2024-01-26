alias cd.='cd `readlink -f .`'
alias cd..='. cd..n'
alias cd...='cd ../..'
alias cd....='cd ../../..'
alias ccd='. cd-ls'
alias cda='cd-any'
alias cd-alias='. cd-alias'

alias md='mkdir'
alias rd='rmdir'

alias  d="ls -oF -N --color --block-size=\"'1\" --time-style='+%y-%m-%d %H:%M:%S'"
alias d.='d -d'
alias dw='. d-which'
alias  l="ls -alF -N --color --block-size=\"'1\" --time-style='+%y-%m-%d %H:%M:%S'"

alias du1='du --max-depth=1'
alias du2='du --max-depth=2'

alias treee='tree-less'

function cd-any() {
    local dir file

	while read dir; do
        cd "$dir"
		return 0
	done < <(find -path ./debian -prune -o -type d -path "*$1*" -print)

	while read file; do
        dir="${file%/*}"
        cd "$dir"
		return 0
	done < <(find -path ./debian -prune -o -type f -path "*$1*" -print)

    _error "can't find *$1*."
    return 1
}

