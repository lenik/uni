@echo off

    rem It is recommended to embed functions of libstr
    rem into your own batch file, to get faster.

    rem Warning:
    rem   ! Ths functions in libstr can't be reentrant !

    if "%_libstr%"=="%1" (
        echo Error: Reentrant is not allowed
        rem goto :EOF
        )

    set _libstr=%1
    shift
    goto %_libstr%
    goto end


    rem ######################################################################
    rem #        LIBSTR - String Library for Batch Script (Windows NT)
    rem # Version:  $Id: libstr.bat,v 1.3 2004-11-30 05:40:16 dansei Exp $
    rem # Notice:   You shall remove the $ in comments in your function copy
    rem # Notice:   You can use characters ' " % ! < > | & in the string* type
    rem #
    rem #                               Index
    rem # ----- /dep ---------------------------------------------------------
    rem #     sn        String Length
    rem #     sl        String Left
    rem #     sr        String Right
    rem #     sb        Substring
    rem #     sx        String Repeat
    rem #     si/sn     String Index (Find)
    rem #     st/sn sl  String Starts-With
    rem #     se/sn sr  String Ends-With
    rem #     ss        String Substitute
    rem #     pj        Path Join
    rem # --------------------------------------------------------------------


    rem ######################################################################
    rem # Function: String Length
    rem # Synopsis: sn(string str)
    rem #$Revision: 1.3 $
:sn
    set _ret=0
    set _sn_str=%~1
:sn_1
    if "%_sn_str%"=="" goto sn_x
    set /a _ret=_ret + 1
    set _sn_str=%_sn_str:~1%
    goto sn_1
:sn_x
    goto end


    rem ######################################################################
    rem # Function: String Left
    rem # Synopsis: sl(string str, int chars)
    rem #$Revision: 1.3 $
:sl
    set _ret=
    set _sl_str=%~1
    set /a _sl_n=%~2
:sl_1
    if "%_sl_n%"=="0" goto sl_x
    if "%_sl_str%"=="" goto sl_x
    set _ret=%_ret%%_sl_str:~0,1%
    set _sl_str=%_sl_str:~1%
    set /a _sl_n=_sl_n - 1
    goto sl_1
:sl_x
    set _sl_str=
    set _sl_n=
    goto end


    rem ######################################################################
    rem # Function: String Right
    rem # Synopsis: sr(string str, int chars)
    rem #$Revision: 1.3 $
:sr
    set _ret=
    set _sr_str=%~1
    set /a _sr_n=%~2
:sr_1
    if "%_sr_n%"=="0" goto sr_x
    if "%_sr_str%"=="" goto sr_x
    set _ret=%_sr_str:~-1%%_ret%
    set _sr_str=%_sr_str:~0,-1%
    set /a _sr_n=_sr_n - 1
    goto sr_1
:sr_x
    set _sr_str=
    set _sr_n=
    goto end


    rem ######################################################################
    rem # Function: Substring
    rem # Synopsis: sb(string str, int start, int length)
    rem #$Revision: 1.3 $
:sb
    set _sb_str=%~1
    set /a _sb_i=%~2
    if "%3"=="" goto sb_1
    set _sb_n=%~3
    set _ret=!_sb_str:~%_sb_i%,%_sb_n%!
    goto sb_x
:sb_1
    set _ret=!_sb_str:~%_sb_i%!
:sb_x
    set _sb_str=
    set _sb_i=
    set _sb_n=
    goto end


    rem ######################################################################
    rem # Function: String Repeat
    rem # Synopsis: sx(string str, int times, string delim)
    rem #$Revision: 1.3 $
:sx
    set _ret=
    set _sx_str=%~1
    set /a _sx_n=%~2
    set _sx_sp=%~3
    if "%_sx_n%"=="0" goto sx_x
    set _ret=%_sx_str%
:sx_1
    set /a _sx_n=_sx_n - 1
    if "%_sx_n%"=="0" goto sx_x
    set _ret=%_ret%%_sx_sp%%_sx_str%
    goto sx_1
:sx_x
    set _sx_str=
    set _sx_sp=
    set _sx_n=
    goto end


    rem ######################################################################
    rem # Function: String Index (Find)
    rem # Synopsis: si(string str, string sub-str)
    rem #$Revision: 1.3 $
:si
    set _si_t=%~1
    set _si_pat=%~2
    call:sn "%_si_t%"
    set _si_max=%_ret%
    call:sn "%_si_pat%"
    set _si_npat=%_ret%
    set /a _si_max=_si_max - _si_npat
    set _si_i=0
    set _si_pos=-1
:si_1
    if %_si_i% gtr %_si_max% goto si_x
    call:sb "%_si_t%" %_si_i% %_si_npat%
    if "%_ret%"=="%_si_pat%" goto si_ok
    set /a _si_i=_si_i + 1
    goto si_1
:si_ok
    set _si_pos=%_si_i%
:si_x
    set _si_t=
    set _si_pat=
    set _si_nt=
    set _si_npat=
    set _si_i=
    set _ret=%_si_pos%
    set _si_pos=
    goto end


    rem ######################################################################
    rem # Function: String Starts-With
    rem # Synopsis: st(string str, string prefix)
    rem #$Revision: 1.3 $
:st
    set _st_str=%~1
    set _st_pat=%~2
    call:sn "%_st_pat%"
    set _st_npat=%_ret%
    call:sl "%_st_str%" "%_st_npat%"
    if "%_ret%"=="%_st_pat%" goto st_ok
    set _ret=0
    goto st_x
:st_ok
    set _ret=1
:st_x
    set _st_str=
    set _st_pat=
    set _st_npat=
    goto end


    rem ######################################################################
    rem # Function: String Ends-With
    rem # Synopsis: se(string str, string suffix)
    rem #$Revision: 1.3 $
:se
    set _se_str=%~1
    set _se_pat=%~2
    call:sn "%_se_pat%"
    set _se_npat=%_ret%
    call:sr "%_se_str%" "%_se_npat%"
    if "%_ret%"=="%_se_pat%" goto se_ok
    set _ret=0
    goto se_x
:se_ok
    set _ret=1
:se_x
    set _se_str=
    set _se_pat=
    set _se_npat=
    goto end


    rem ######################################################################
    rem # Function: String Substitute
    rem # Synopsis: ss(string str, char source, string target)
    rem #$Revision: 1.3 $
:ss
    set _ss_src=%~1
    set _ss_from=%~2
    set _ss_to=%~3
    set _ret=
:ss_1
    if "%_ss_src%"=="" goto ss_x
    set _ss_chr=%_ss_src:~0,1%
    set _ss_src=%_ss_src:~1%
    if "%_ss_chr%"=="%_ss_from%" (
        set _ret=%_ret%%_ss_to%
    ) else (
        set _ret=%_ret%%_ss_chr%
    )
    goto ss_1
:ss_x
    set _ss_chr=
    set _ss_src=
    set _ss_from=
    set _ss_to=
    goto end


    rem ######################################################################
    rem # Function: Path Join
    rem # Synopsis: pj(string path1, string path2)
    rem # Return:   path1\path2 (with no trialing slash)
    rem #$Revision: 1.3 $
:pj
    set _ret=%~2
    if "%_ret:~1,1%"==":" goto pj_x
    if "%_ret:~0,1%"=="\" goto pj_x
    if "%_ret:~0,1%"=="/" goto pj_x
    set _pj_a=%~1
    set _pj_b=%~2
    if "%_pj_a:~-1%"=="\" set _pj_a=%_pj_a:~0,-1%
    if "%_pj_a:~-1%"=="/" set _pj_a=%_pj_a:~0,-1%
    set _ret=%_pj_a%\%_pj_b%
:pj_x
    set _pj_a=
    set _pj_b=
    goto end


:end
    set _libstr=
