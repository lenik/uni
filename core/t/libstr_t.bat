@echo off


	set _ckall=0
	set _ckfail=0

	echo LIBSTR Test Suite
	echo.

:test_sn
	call libstr sn hello
		call:check 5 libstr::sn.1
	call libstr sn "What ever you are."
		call:check 18 libstr::sn.2
	call libstr sn "~@#$*()_+"
		call:check 9 libstr::sn.3


:test_sl
	call libstr sl "La Arboj estas grandaj. " 8
		call:check "La Arboj" libstr::sl.1
	call libstr sl Esperantisto 3
		call:check Esp libstr::sl.2
	call libstr sl " 123 " 10
		call:check " 123 " libstr::sl.3
	call libstr sl " 123 " 0
		call:check "" libstr::sl.4

:test_sr
	call libstr sr "La Arboj estas grandaj. " 8
		call:check "randaj. " libstr::sr.1
	call libstr sr Esperantisto 3
		call:check sto libstr::sr.2
	call libstr sr " 123 " 10
		call:check " 123 " libstr::sr.3
	call libstr sr " 123 " 0
		call:check "" libstr::sr.4

:test_sb
	call libstr sb "La Arboj estas grandaj. " 8 3
		call:check " es" libstr::sb.1
	call libstr sb Esperantisto 3 10
		call:check erantisto libstr::sb.2
	call libstr sb " 123 " 0 8
		call:check " 123 " libstr::sb.3
	call libstr sb " 123 " 9 3
		call:check "" libstr::sb.4

:test_sx
	call libstr sx * 5 .
		call:check "*.*.*.*.*" libstr::sx.1
	call libstr sx * 0 ...
		call:check "" libstr::sx.2
	call libstr sx oOo 2 -
		call:check "oOo-oOo" libstr::sx.3
	call libstr sx "Oh Hahaha" 1 -
		call:check "Oh Hahaha" libstr::sx.4

:test_si
	call libstr si "Where is my home over there?" home
		call:check 12 libstr::si.1
	call libstr si " Nothing here " anything
		call:check -1 libstr::si.2
	call libstr si "Noo hoopes, o here not." "o h"
		call:check 2 libstr::si.3
	call libstr si "small" "small...BIG"
		call:check -1 libstr::si.4

:test_st
	call libstr st "Where is my home over there?" Where
		call:check 1 libstr::st.1
	call libstr st " Nothing here " anything
		call:check 0 libstr::st.2
	call libstr st "Noo hoopes, o here not." ""
		call:check 1 libstr::st.3
	call libstr st "Th" "Th........"
		call:check 0 libstr::st.4

:test_se
	call libstr se "Where is my home over there?" there?
		call:check 1 libstr::se.1
	call libstr se " Nothing here " anything
		call:check 0 libstr::se.2
	call libstr se "Noo hoopes, o here not." ""
		call:check 1 libstr::se.3
	call libstr se "Th" ".....Th"
		call:check 0 libstr::se.4

:test_ss
	call libstr ss a-b-c-d - :
		call:check a:b:c:d libstr::ss.1
	call libstr ss "He is *, and she is also *. " * Snima
		call:check "He is Snima, and she is also Snima. " libstr::ss.2

:test_pj
	call libstr pj c:\abcd\def\g d:\as\fds\nn
		call:check d:\as\fds\nn libstr::pj.1
	call libstr pj c:\abcd\def\g as\fds\nn
		call:check c:\abcd\def\g\as\fds\nn libstr::pj.2
	call libstr pj c:\123\456\ 789\xyz
		call:check c:\123\456\789\xyz libstr::pj.3
	call libstr pj c:\x3\fas\55\ \xz\hwmx\\3
		call:check \xz\hwmx\\3 libstr::pj.4


:test_summary
	set /a _ckpass=_ckall - _ckfail
	echo Total %_ckall% tests, %_ckpass% passed, %_ckfail% failed.
	set _ckall=
	set _ckpass=
	set _ckfail=
	goto end


:check
	set _ck=%~1
	set _ckt=%~2
	if "%_ckt%"=="" set _ckt=(unnamed)
	if not "%_ck%"=="%_ret%" (
		echo Test of %_ckt% failed, expected for "%_ck%" / "%_ret%"
		set /a _ckfail=_ckfail + 1
	) else (
		echo Test of %_ckt% succeed.
	)
	set /a _ckall=_ckall + 1
	set _ck=
	set _ckt=
	goto end


:end
