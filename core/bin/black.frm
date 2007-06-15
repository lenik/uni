VERSION 5.00
Begin VB.Form black
   BackColor       =   &H00000000&
   BorderStyle     =   0  'None
   Caption         =   "Form1"
   ClientHeight    =   2880
   ClientLeft      =   0
   ClientTop       =   0
   ClientWidth     =   3480
   LinkTopic       =   "Form1"
   ScaleHeight     =   2880
   ScaleWidth      =   3480
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  'Windows Default
   WindowState     =   2  'Maximized
End
Attribute VB_Name = "black"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private clrs As New Dictionary

Private Sub Form_KeyPress(KeyAscii As Integer)
    Dim c As String
    Select Case Chr(KeyAscii)
    Case "q"
        End
    End Select
End Sub

Private Sub Form_Load()
    init

    Dim clr
    clr = Command
    If clr <> "" Then
        If clrs.Exists(clr) Then
            clr = clrs(clr)
        ElseIf Right(clr, 1) = "h" Then
            clr = Hex(clr)
        Else
            clr = Val(clr)
        End If
        BackColor = clr
    End If
End Sub

Sub init()
    clrs.Add "black", &H0&
    clrs.Add "red", &HFF&
    clrs.Add "green", &HFF00&
    clrs.Add "blue", &HFF0000
End Sub
