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

* * abc.d
* * bin
* * etc
* * home
* * lib
* * local
* * usr

z z z
