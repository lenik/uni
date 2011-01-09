; Hot Return DOS Enviroment Utitlity    DanSei 1998.11.10

        .Model Tiny
        .286
        .Code
        Org 100h

Start:
        Mov     AX, 3505h
        Int     21h
        Mov     PrnOff, BX
        Mov     PrnSeg, ES

        Cmp     BX, Offset NewPrn
        Jnz     Install
        Mov     AH, 9h
        Lea     DX, Once
        Int     21h

        Int     20h

        PrnOff  DW 0
        PrnSeg  DW 0
        Once    DB 'Term Has Already Been Installed !  Type Term /Q Removed.', 0Dh, 0Ah, '$'
        Insted  DB 'Term Has Been Successfully Installed !', 0Dh, 0Ah, '$'

Install:
        Push    ES
        Mov     AX, 3505h
        Int     21h
        Mov     Word Ptr CS: OriPrn, BX
        Mov     Word Ptr CS: OriPrn + 2, ES
        Mov     AX, 351Ch
        Int     21h
        Mov     Word Ptr CS: Ori1C, BX
        Mov     Word Ptr CS: Ori1C + 2, ES
        Mov     AH, 34h
        Int     21h
        Mov     Word Ptr CS: Indos, BX
        Mov     Word Ptr CS: Indos + 2, ES
        Pop     ES

        Cli
        Mov     AX, 2505h
        Lea     DX, NewPrn
        Int     21h
        Mov     AX, 251Ch
        Lea     DX, New1C
        Int     21h
        Sti

        Mov     AH, 9
        Lea     DX, Insted
        Int     21h
        Lea     DX, TheEnd
        Inc     DX
        Int     27h

OriPrn  DD      0
Ori1C   DD      0

Indos   DD      0

NewPrn  Proc
        Push    ES
        Push    BX
        Cmp     Byte Ptr CS: Busy, 0
        Jz      Continue
        Jmp     Short PrnExit

Continue:
        Cli
        Add     SP, 8                   ; Pop FL, IP, CS, FL
        Mov     Byte Ptr CS: Busy, 1
        Mov     Byte Ptr CS: TimerEnabled, 1
        Sti
        Les     BX, CS: Indos
        Cmp     Byte Ptr ES: [BX], 0
        Jnz     PrnExit
        Mov     AH, 4Ch
        Int     21h
        Busy    DB 0
PrnExit:
        Mov     Byte Ptr CS: Busy, 0
        Pop     BX
        Pop     ES
        Iret
NewPrn  Endp

New1C   Proc
        Cmp     Byte Ptr CS: TimerEnabled, 0
        Jz      New1CExit
        Inc     Byte Ptr CS: Busy
        Cmp     Byte Ptr CS: Busy, 20
        Jb      New1CExit
        Mov     Byte Ptr CS: Busy, 0
        Mov     Byte Ptr CS: TimerEnabled, 0
New1CExit:
        Jmp     DWord Ptr CS: Ori1C
        TimerEnabled    DB 1
New1C   Endp

TheEnd  Label

        End     Start
