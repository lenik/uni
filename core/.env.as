# <VAR> <OPERATION> <PARAM>
# OPERATION:
#	=	set VAR=PARAM
#	+	set VAR=${VAR}PARAM
#	-	set VAR=PARAM${VAR}
#	p	set VAR=${VAR}[:;]PARAM
#	l	set VAR=`readlink -f PARAM`
#	lp	set VAR=${VAR}[:;]`readlink -f PARAM`
#	lp-	set VAR=`readlink -f PARAM`[:;]${VAR}
#	u	unset VAR
#	*	recurse into sub-directory's PARAM/.env.as
#	z	end of list

PERLLIB         lp-     lib
PYTHONPATH      lp-     lib

* * etc
* * proc
* * usr
* * lib
* * abc.d
* * local
* * home
z z z
