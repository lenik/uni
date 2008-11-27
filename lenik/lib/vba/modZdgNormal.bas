Attribute VB_Name = "modZdgNormal"

Enum SelectionExportType
    SET_FirstRowAsName = 0
    SET_NewDocument
End Enum


Public Sub ZdgSettings()
    With ActiveDocument
        .ShowGrammaticalErrors = False
        .ShowSpellingErrors = False
        .SnapToGrid = False
    End With
End Sub


' Hard page means it's broke thru "Page Break" Character.
' Return the page no.
Public Function SelectCurrentHardPage() As Long
    Dim cPageBreak As String
    cPageBreak = Chr(12)

    Dim nLefted As Long
    Dim nRighted As Long
    nLefted = -Selection.MoveStartUntil(cPageBreak, wdBackward) - 1
    nRighted = Selection.MoveEndUntil(cPageBreak, wdForward) - 1

    ' The page number will +1 after included the page break char.
    SelectCurrentHardPage = Selection.Information(wdActiveEndPageNumber)

    If nLefted = -1 Then
        ' Left pass BOF and found no result
        '   Selection.MoveStart wdCharacter, ActiveDocument.Characters.Count
        Selection.MoveStart wdStory, -1
    Else
        ' Found left "Page Break",
        ' but don't include that character.
    End If

    If nRighted = -1 Then
        ' Right pass EOF and found no result
        '   Selection.MoveEnd wdCharacter, ActiveDocument.Characters.Count
        Selection.MoveEnd wdStory, 1
    Else
        ' Found right "Page Break", move right 1 unit again
        ' Use characters count to make sure selection extend to the entire document.
        Selection.MoveEnd wdCharacter, 1
    End If
End Function


Public Function GetFirstLine(text As String, _
                              Optional ByVal bTheWord As Boolean = False, _
                              Optional ByVal cDel As Integer = 13) As String
    Dim firstLineOffset As Long
    Dim l As Long
    Dim result As String
    firstLineOffset = InStr(text, Chr(cDel))
    If firstLineOffset > 0 Then
        result = Trim(Left(text, firstLineOffset - 1))
        If result = "" Then
            ' first line have no words! then scan every line.
            Dim lines, l1, l2
            lines = Split(text, Chr(cDel))
            l1 = LBound(lines)
            l2 = UBound(lines)
            result = ""
            For l = l1 To l2
                result = Trim(lines(l1))
                If result <> "" Then
                    Exit For
                End If
            Next
        End If
    Else
        result = Trim(text)
    End If

    If result = "" Then
        GetFirstLine = ""
        Exit Function
    End If

    If bTheWord Then
        result = Replace(result, vbTab, " ")
        result = GetFirstLine(result, False, Asc(" "))
    End If

    GetFirstLine = result
End Function

Public Sub ExportSelection(Optional ByVal ExpType As SelectionExportType = SET_FirstRowAsName, _
                           Optional ByVal FileType As WdSaveFormat = wdFormatDocument, _
                           Optional ByVal sPrefix As String = "")
    Dim doc As Document

    Select Case ExpType
    Case SET_FirstRowAsName
        Dim sName As String
        sName = GetFirstLine(Selection.text)

        Static bNoPrompt As Boolean
        If sName = "" Then
            If Not bNoPrompt Then
                Select Case MsgBox("Name undefined! Click 'No' to disable this message", _
                    vbInformation + vbYesNoCancel)
                Case vbNo
                    bNoPrompt = True
                Case vbCancel
                    ' throw exception.
                End Select
            End If
            Exit Sub
        End If

        Selection.Copy
        Set doc = Application.Documents.Add
        doc.Range.Paste
        doc.SaveAs sPrefix & sName, FileType
        doc.Close
    Case SET_NewDocument
        Selection.Copy
        Set doc = Application.Documents.Add
        'doc.summary=sprefix&sname
        doc.Range.Paste
    End Select
End Sub

Public Sub ExpSel_SplitPages()
    ' The BOF of document.
    Dim iPage As Long
    Dim cPages As Long

    'Dim sel As Selection
    'Set sel = ActiveDocument.Windows(1).Selection

    cPages = Selection.Information(wdNumberOfPagesInDocument)

    Selection.Move wdStory, -1
    Do
        iPage = SelectCurrentHardPage

        ExportSelection SET_FirstRowAsName, wdFormatDocument, Trim(Str(iPage)) & String(4 - Len(Trim(Str(iPage))), "_")

        Selection.Move wdCharacter, 1
    Loop While iPage < cPages
End Sub
