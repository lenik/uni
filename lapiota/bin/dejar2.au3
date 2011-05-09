#include "util.au3"

if $CmdLine[0] < 2 then
    alert("dejar2 <jar-file> <dst-zip>")
    exit 1
endif

run("jd-gui " & $CmdLine[1])
winWaitActive("[CLASS:wxWindowClassNR; TITLE:Java Decompiler]")
send("^!s")
winWaitActive("Save")
    beat()
    controlSetText("Save", "", 1148, $CmdLine[2])
    ; ControlClick("Save", "", 1)
    send("!s")

    ; overwrite? ID: YES 6, NO 7
    beat()
    send("!y")

winWaitClose("Save Jar sources")
send("!x")

if $CmdLine[0] >= 3 then
    run($CmdLine[3] & " " & $CmdLine[2])
endif
