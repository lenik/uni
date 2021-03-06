
' Repair registry destroyed by fucking STHVCD:
'   HKCR\.vbs = "VBSFile"
'

' note.vbs syntax:
'	note <note-home> <command> <options>
' command:
' 	create <ftype> <path>
'	...

	set sh=createobject("Wscript.shell")
	set args=wscript.arguments

	notevol=args(0)

	select case lcase(args(1))
	case "create"
		note_create args(2), args(3)
	case else
		msgbox "Error command: " & args(1)
	end select


sub note_create(ftype, path)
	select case lcase(ftype)
	case "doc"
		note_create_word notevol & "\.vol-def.doc", path
	case "xls"
		note_create_excel notevol & "\.vol-def.xls", path
	case else
		msgbox "Error File Type: " & ftype
	end select
end sub

sub note_create_word(temp, path)
	dim app, doc
	set app=createobject("Word.Application")
	set doc=app.documents.add(temp)
	doc.saveas path
	doc.close
end sub

sub note_create_excel(temp, path)
	dim app, doc
	set app=createobject("Excel.Application")
	set doc=app.workbooks.add(temp)
	doc.saveas path
	doc.close
end sub
