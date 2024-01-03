// import * as Str from '../lang/string';

interface DirBase {
    dir: string
    base: string
}

export function splitDirBase(path: string): DirBase {
    let lastSlash = path.lastIndexOf('/');
    let dir, base;
    if (lastSlash == -1) {
        dir = '.';
        base = path;
    } else {
        dir = path.substring(0, lastSlash);
        base = path.substring(lastSlash + 1);
    }
    return { "dir": dir, "base": base };
}


export function baseName(s: string): string {
    let lastSlash = s.lastIndexOf('/');
    return lastSlash == -1 ? s : s.substring(lastSlash + 1);
}

export function toAbsoluteUrl(href: string): string {
    let context = splitDirBase(document.URL).dir;
    while (href.startsWith("../")) {
        href = href.substring(3);
        var db = splitDirBase(context);
        if (db.dir != null)
            context = db.dir;
    }
    var url = context + "/" + href;
    return url;
}

export function alterHref(href: string): string {
    return alterHrefBase(href, window.hrefOverride);
}

/**
 * Replace href with the given base.
 *
 * @param href
 * @param newBase should be absolute url, like `http://...`
 */
export function alterHrefBase(href: string, newBase: string): string {
    if (newBase == null || newBase == '') // disabled.
        return href;
    if (href.indexOf(":") != -1) // ignore absolute href.
        return href;

    var debug = false;
        if (location.protocol == 'file:') debug = true;
        switch (location.hostname) {
            case "localhost":
            case "172.22.8.1":
                debug = true;
        }
    if (! debug)
        return href;

    if (href.charAt(0) != "/") {
        // href is relative-path.
        var url = toAbsoluteUrl(href);
        
        var expected = "/libjs/lenik/dev-local.js";
        if (! __FILE__.endsWith(expected)) {
            throw "invalid dev-local.js location: " + __FILE__;
        }
        var localRoot = __FILE__.substr(0, __FILE__.length - expected.length);
        var nprefix = localRoot.length;
        if (url.substring(0, nprefix) != localRoot)
            throw "unexpected";
        
        href = url.substring(nprefix);
    }
    
    var ss = newBase.indexOf("://"); // assert true
    var slash = newBase.indexOf("/", ss + 3);
    if (slash != -1) // Trim to "http://....com:123".
        newBase = newBase.substring(0, slash);

    if (newBase.substring(newBase.length -1) == "/")
        newBase = newBase.substring(0, newBase.length - 1);
    return newBase + href;
}

if (document.currentScript != null) {
    let scriptSrc = document.currentScript.getAttribute("src");
    
    window.__FILE__ = toAbsoluteUrl(scriptSrc);
}
