/*
COPY FOLLOWING HEADER TO YOUR APPLICATION:
    var ENV     = _new('WScript.Shell').Environment;
    var FSO     = _new('Scripting.FileSystemObject');
    eval(FSO.OpenTextFile(ENV('LAPIOTA') + '/lib/wsh/boot.js').ReadAll());

ALSO CHANGE DEFAULT SCRIPT ENGINE:
    CScript //H:CScript //NoLogo //S
 */
var FSO     = _new('Scripting.FileSystemObject');
var SHELL   = _new('WScript.Shell');
var ENV     = SHELL.Environment;

var __FILE__= WScript.ScriptFullName;
var __DIR__;
    if ((i = __FILE__.lastIndexOf('\\')) != -1)
        __DIR__ = __FILE__.substring(0, i);
    else
        __DIR__ = '.';
var CWD     = SHELL.CurrentDirectory;
var LIBDIR  = ENV('LAPIOTA') + '/lib';

var ARGV    = new Array();
var ARGC    = WScript.Arguments.Length;
    for (i = 0; i < ARGC; i++)
        ARGV[i] = WScript.Arguments(i);

function _new(progId) {
    var obj = WScript.CreateObject(progId);
    return obj;
}

function echo(s) {
    WScript.echo(s);
}

function echoError(s) {
    WScript.echo(s);
}

function sleep(ms) {
    WScript.Sleep(ms);
}

function exit(n) {
    if (n == undefined) n = 1;
    WScript.Quit(n);
}

function fatal(s, n) {
    echoError('Fatal Error: ' + s);
    exit(n);
}

function run(s) {
    SHELL.run(s);
}

function dump() {
    var args = arguments;
    var e;
    echo('ENV: ' + ENV.Length);
    e = new Enumerator(ENV);
    while (!e.atEnd()) {
        echo('    ' + e.item());
        e.moveNext();
    }

    echo();
    echo('ARGUMENTS: ' + ARGV.length);
    for (i = 0; i < ARGV.length; i++)
        echo('    ' + i + '. ' + ARGV[i]);

    echo();
    echo('WScript.BuildVersion  = ' + WScript.BuildVersion);
    echo('WScript.FullName      = ' + WScript.FullName);
    echo('WScript.Interactive   = ' + WScript.Interactive);
    echo('WScript.Name          = ' + WScript.Name);
    echo('WScript.Path          = ' + WScript.Path);
    echo('WScript.ScriptFullName= ' + WScript.ScriptFullName);
    echo('WScript.ScriptName    = ' + WScript.ScriptName);
    echo('WScript.Version       = ' + WScript.Version);

    echo();
    echo('__DIR__   = ' + __DIR__);
    echo('__FILE__  = ' + __FILE__);
    echo('CWD       = ' + CWD);
    echo('LIBDIR    = ' + LIBDIR);

    echo();
    echo('Debug arguments: ');
    for (i = 0; i < args.length; i++)
        echo('    ' + i + '. ' + args[i]);

    exit(1);
}

function loadFile(path, encoding) {
    if (encoding == undefined) encoding = 'utf-8';
    var _in = _new('ADODB.Stream');
    _in.Open();
    _in.Charset = encoding;
    _in.LoadFromFile(path);
    var content = _in.ReadText();
    _in.Close();
    return content;
}

function saveFile(path, encoding, content) {
    if (encoding == undefined) encoding = 'utf-8';
    var out = _new('ADODB.Stream');
    out.Open();
    out.Charset = encoding;
    out.WriteText(content);
    out.SaveToFile(path);
    out.Close();
}

function include(path) {
    var content = loadFile(path, 'utf-8');
    try {
        eval(content);
        return content;
    } catch (e) {
        var mesg = 'Failed to load ' + path
            + ': ' + e.name + '(' + e.number + ')';
        if (e.description != '')
            mesg += '\n    ' + e.description;
        fatal(mesg);
    }
}


function _import(libname) {
    var path = LIBDIR + '/' + libname.replace('.', '/') + '.js';
    // echo('Import ' + path);
    if (! FSO.FileExists(path))
        fatal('No such library: ' + libname);
    return include(path, 'utf-8');
}

function assert(e, m) {
    if (!e) {
        if (m != undefined) m = ': ' + m;
        echoError('assert error' + m);
        exit(1);
    }
}

function join(IFS, v) {
    var i, s = '';
    for (i = 0; i < v.length; i++) {
        if (i != 0)
            s += IFS;
        s += v[i];
    }
    return s;
}
0;
