VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
  Persistable = 0  'NotPersistable
  DataBindingBehavior = 0  'vbNone
  DataSourceBehavior  = 0  'vbNone
  MTSTransactionMode  = 0  'NotAnMTSObject
END
Attribute VB_Name = "Options"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = True
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
Option Explicit
Option Base 0

Private Const MAX_ARGS As Integer = 10000

Private m_cmdline As String
Private m_args(0 To MAX_ARGS) As String
Private m_nargs As Integer
Private m_lquot As Boolean

Public Sub Parse(ByVal cmdline As String)
    m_cmdline = cmdline
    Dim i As Integer
    Dim c As String
    Dim st As Integer
    Dim lastarg As String

    For i = 1 To Len(cmdline)
        c = Mid(cmdline, i, 1)
        Select Case st
        Case 0 ' start
            If c = " " Then
                st = 1
            Else
                lastarg = lastarg & c
            End If
        Case 1 ' skipping
            If c <> " " Then
                st = 0
                addarg lastarg
                lastarg = c
            End If
        End Select
    Next
    If lastarg <> "" Then addarg lastarg
End Sub

Private Sub addarg(ByVal arg As String)
    If m_lquot Then
        If Right(arg, 1) = """" Then
            arg = Left(arg, Len(arg) - 1)
            m_lquot = False
        End If
        m_args(m_nargs - 1) = m_args(m_nargs - 1) & " " & arg
    Else
        If Left(arg, 1) = """" Then
            arg = Mid(arg, 2)
            m_lquot = Right(arg, 1) <> """"
        End If
        m_args(m_nargs) = arg
        m_nargs = m_nargs + 1
    End If
End Sub

Public Property Get Count() As Integer
    Count = m_nargs
End Property

Public Property Get Item(ByVal index As Integer) As String
Attribute Item.VB_UserMemId = 0
    If index < 0 Or index >= m_nargs Then
        Err.Raise -1, "index out of range: " & index
    End If
    Item = m_args(index)
End Property
