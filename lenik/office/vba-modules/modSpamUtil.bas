Attribute VB_Name = "modSpamUtil"

Option Base 0
Option Explicit


Public Function getMapiFolder(ByVal fpath) As MAPIFolder
    Dim segs, s
    Dim f As Object     ' dispatch object' features

    fpath = Trim(fpath)
    Set f = ThisOutlookSession.Session

    segs = Split(fpath, "/")
    For Each s In segs
        s = Trim(s)
        If s <> "" Then Set f = f.Folders(s)
    Next

    Set getMapiFolder = f
End Function

Public Sub IgnoreFolder(Optional fname)
    Dim fGarbage As Outlook.MAPIFolder
    Dim fItem As MailItem

    If IsMissing(fname) Then fname = "个人文件夹/垃圾邮件"

    Set fGarbage = getMapiFolder(fname)

    For Each fItem In fGarbage.Items
        Debug.Print fItem

    Next
End Sub

Public Function exists(col As Collection, ByVal item As Variant)
    Dim e As Variant
    For Each e In col
        If IsObject(item) Then
            If item Is e Then
                exists = True
                Exit Function
            End If
        Else
            If item = e Then
                exists = True
                Exit Function
            End If
        End If
    Next
    exists = False
End Function

Public Function getAccounts() As Collection
    Dim so As SyncObject
    Set getAccounts = New Collection
    For Each so In ThisOutlookSession.Session.SyncObjects
        ' TODO: get the email of sync-object
        getAccounts.Add so
    Next
End Function

Public Sub SpamChecker(Optional fin, Optional fout)
    Dim fInput As Outlook.MAPIFolder
    Dim fGarbage As Outlook.MAPIFolder
    Dim fItem As Outlook.MailItem
    Dim myAccounts As Collection

    If IsMissing(fin) Then fin = "个人文件夹/收件箱"
    If IsMissing(fout) Then fout = "个人文件夹/垃圾邮件"

    Set fInput = getMapiFolder(fin)
    Set fGarbage = getMapiFolder(fout)

    Set myAccounts = getAccounts

    For Each fItem In fInput.Items
        Dim tt
        If exists(myAccounts, fItem.To) Then
        End If
    Next
End Sub
