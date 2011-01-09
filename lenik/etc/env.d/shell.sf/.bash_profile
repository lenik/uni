# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
	. ~/.bashrc
fi

# User specific environment and startup programs

PATH=$PATH:$HOME/bin:$HOME/bin/0

JAVA_HOME=/usr/java/j2sdk1.4.2_01
PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin
CLASSPATH=.:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar

PERL5LIB=$PERL5LIB:$HOME/bin/0/lib

export PATH JAVA_HOME CLASSPATH PERL5LIB
unset USERNAME

alias cd..='cd ..'
alias cd...='cd ../..'
alias cd.='cd .'
alias rm='rm -f'

export  CHSID=/home/groups/c/ch/chsid
export   DBCP=/home/groups/d/db/dbcp
export EOOODB=/home/groups/e/eo/eooodb
export     FJ=/home/groups/f/fr/freejava
export  IMATH=/home/groups/i/im/imath
export  LIIIL=/home/groups/l/li/liiil
export   MEGO=/home/groups/m/me/mego
export    MP3=/home/groups/m/mp/mp-3
export    MSB=/home/groups/m/ms/msb
export    XJL=/home/groups/x/xj/xjl
export   PHPJ=/home/groups/p/ph/phpj

alias cdj='cd /home/groups/x/xj/xjl'
alias cdl='cd /home/groups/l/la/lapiota'
alias cds='cd /home/groups/c/ch/chsid'
alias cdm='cd /home/groups/m/me/mego'
alias cdu='cd /home/groups/e/eo/eooodb'
alias cd3='cd /home/groups/m/mp/mp-3'
alias cd3r='cd /home/groups/m/mp/mp3r'
alias cdfj='cd /home/groups/f/fr/freejava'
alias cdmsb='cd /home/groups/m/ms/msb'
alias cdd='cd /home/groups/d/db/dbcp'
alias xjl='echo This is xima lenik, the command xjl is used for test-purpose.'

# the server of sourceforge.net is fast.

alias paths='echo $PATH|tr : "\n"'
