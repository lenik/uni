module lenik.bas.file;

import std.file : exists, isDir, isFile, isSymlink, readLink;
import std.path : absolutePath, dirName, buildNormalizedPath;
import std.string : endsWith;

bool isdir(string path) {
    return exists(path) && isDir(path);
}

bool isfile(string path) {
    return exists(path) && isFile(path);
}

bool issymlink(string path) {
    return exists(path) && isSymlink(path);
}

string canonicalize(string path) {
    path = absolutePath(path);

    while (exists(path) && isSymlink(path)) {
        string ctx;
        if (path.endsWith("/"))
            ctx = path;
        else
            ctx = dirName(path);
        string target = readLink(path);
        path = buildNormalizedPath(ctx, target);
    }
    
    return path;
}
