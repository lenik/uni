VERSION 5.00
Begin VB.Form frmLogo
   BorderStyle     =   0  'None
   Caption         =   "Logo"
   ClientHeight    =   2880
   ClientLeft      =   0
   ClientTop       =   0
   ClientWidth     =   3330
   LinkTopic       =   "Form1"
   ScaleHeight     =   2880
   ScaleWidth      =   3330
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  'Windows Default
   Begin VB.Timer DelayExit
      Left            =   1200
      Top             =   60
   End
   Begin VB.PictureBox pic
      Appearance      =   0  'Flat
      AutoSize        =   -1  'True
      BackColor       =   &H80000005&
      BorderStyle     =   0  'None
      ForeColor       =   &H80000008&
      Height          =   615
      Left            =   0
      ScaleHeight     =   41
      ScaleMode       =   3  'Pixel
      ScaleWidth      =   41
      TabIndex        =   0
      Top             =   0
      Visible         =   0   'False
      Width           =   615
   End
   Begin VB.Image img
      Height          =   975
      Left            =   0
      Top             =   0
      Width           =   1095
   End
End
Attribute VB_Name = "frmLogo"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Dim opts As New Options
Dim opt_radius As Integer
Dim opt_file As String
Dim opt_delay As Integer

Private Sub DelayExit_Timer()
    End
End Sub

Private Sub Form_Load()
    opts.Parse Command

    opt_delay = 2000

    Dim i As Integer
    While i < opts.Count
        Select Case opts(i)
        Case "-r", "--radius"
            i = i + 1
            opt_radius = Val(opts(i))
        Case "-d", "--delay"
            i = i + 1
            opt_delay = Val(opts(i))
        Case "-f", "--file"
            i = i + 1
            opt_file = Trim(opts(i))
        Case "-h", "--help"
            MsgBox "logo -r=RADIUS -d=DELAY -f=FILE", vbInformation
            End
        End Select
        i = i + 1
    Wend

    Set pic.Picture = LoadPicture(opt_file)
    Me.Width = pic.Width
    Me.Height = pic.Height
    Me.Left = (Screen.Width - Me.Width) / 2
    Me.Top = (Screen.Height - Me.Height) / 2

    Set img.Picture = pic.Picture

    Dim hrgn As Long
    hrgn = GDI.CreateRoundRectRgn(0, 0, _
        pic.ScaleWidth, pic.ScaleHeight, opt_radius, opt_radius)
    GDI.SelectObject hDC, hrgn

    DelayExit.Interval = opt_delay
End Sub
