alias mvn='coolmvn'
alias mpk='mvn package'
alias mte='mvn test'
alias min='mvn install'
alias mdp='mvn deploy'

alias MPK='nohup mvn package -fn -Dmaven.test.skip=true'
alias MIN='nohup mvn install -fn -Dmaven.test.skip=true'
alias MDP='nohup mvn deploy -fn -Dmaven.test.skip=true'

alias M+='m2release'
alias 'mr-'='cd.x mvn release:prepare'
alias mr+='cd.x mvn release:perform'
alias mr0='cd.x mvn release:rollback'

alias M='m2chdir'

function m2chdir() {
    local path
    while read path; do
        cd "$path"
        return
    done < <(m2which -l "$@")
    echo "Not found: $@"
}
