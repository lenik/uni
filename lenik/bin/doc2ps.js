// BOOT HEADER BEGIN
    var ENV     = new ActiveXObject('WScript.Shell').Environment;
    var FSO     = new ActiveXObject('Scripting.FileSystemObject');
    eval(FSO.OpenTextFile(ENV('LAPIOTA') + '/lib/wsh/boot.js').ReadAll());
// BOOT HEADER END

eval(_import('wsh.path'));

if (ARGC < 2) {
    echo('Usage: doc2ps <SRC.doc> <DST-FILE.ps/DIR>');
    exit();
}

var docFile = pathJoin(CWD, ARGV[0]);
var psFile = pathJoin(CWD, ARGV[1]);
    if (isDir(psFile)) {
        var docBase = baseName(docFile);
        var dot = docBase.lastIndexOf('.');
        assert(dot != 0);
        var docName = docBase.substring(0, dot);
        psFile += docName + '.ps';
    }

echo('Convert ' + docFile + ' ¡ú ' + psFile);

var app = new ActiveXObject('Word.Application');
var doc = app.Documents.Open(docFile);

try {
    //doc.ActivePrinter = 'Acrobat Distiller';
    doc.PrintOut(
        false,      // background print job
        false,      // append to output file
        0,          // range = wdPrintAllDocument
        psFile,     // output file name
        0, 0,       // from, to
        0, 1, 0,   // item, copies, pages
        true       // print to file
        // true,       // collate for multiple copies
        );
} finally {
    doc.close();
    app.Quit(false);
}

echo('    OK.');
