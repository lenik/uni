// BOOT HEADER BEGIN
    var ENV     = new ActiveXObject('WScript.Shell').Environment;
    var FSO     = new ActiveXObject('Scripting.FileSystemObject');
    eval(FSO.OpenTextFile(ENV('LAPIOTA') + '/lib/wsh/boot.js').ReadAll());
// BOOT HEADER END
