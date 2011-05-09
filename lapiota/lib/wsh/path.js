
function pathJoin(a, b) {
    if (a == undefined || a == '') return b;
    if (b == undefined || b == '' || b == '.') return a;
    if ((b.indexOf(':') != -1)
            || b.charAt(0) == '/'
            || b.charAt(0) == '\\')
        return b;
    var aEnd = a.charAt(a.length - 1);
    if (aEnd != '/' && aEnd != '\\')
        a += '/';
    return a + b;
}

function dirName(path) {
    var i = path.lastIndexOf('/');
    var j = path.lastIndexOf('\\');
    if (i == -1)
        if (j == -1)
            return '.';
        else
            i = j;
    else if (j > i)
        i = j;
    return path.substring(0, i);
}

function baseName(path) {
    var i = path.lastIndexOf('/');
    var j = path.lastIndexOf('\\');
    if (i == -1)
        if (j == -1)
            return path;
        else
            i = j;
    else if (j > i)
        i = j;
    return path.substring(i + 1);
}

function isFile(path) {
    return FSO.FileExists(path);
}

function isDir(path) {
    return FSO.FolderExists(path);
}

function getParent(path) {
    return FSO.GetParentFolderName(path);
}
