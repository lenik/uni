if (String.prototype.startsWith == undefined)
    String.prototype.startsWith = function(s) {
        if (s == null) return false;
        var n = s.length;
        return this.substring(0, n) == s;
    };

switch (location.protocol) {
    case "file:":
        if (location.pathname.startsWith("/android_asset/"))
            ; // debug = debug;
        else
            window.debug = true;
        break;
    case "http:":
    case "https:":
        if (location.host == "localhost")
            window.debug = true;
        break;
}

function splitDirBase(path) {
    var lastSlash = path.lastIndexOf('/');
    var dir, base;
    if (lastSlash == -1) {
        dir = null;
        base = path;
    } else {
        dir = path.substr(0, lastSlash);
        base = path.substr(lastSlash + 1);
    }
    return { "dir": dir, "base": base };
}

function toAbsoluteUrl(href) {
    var context = splitDirBase(document.URL).dir;
    while (href.startsWith("../")) {
        href = href.substr(3);
        var db = splitDirBase(context);
        if (db.dir != null)
            context = db.dir;
    }
    var url = context + "/" + href;
    return url;
}

function alterHref(href) {
    return alterHrefBase(href, window.hrefOverride);
}

var __FILE__ = toAbsoluteUrl(document.currentScript.getAttribute("src"));

/**
 * Replace href with the given base.
 *
 * @param href
 * @param newBase should be absolute url, like `http://...`
 */
function alterHrefBase(href, newBase) {
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
        if (url.substr(0, nprefix) != localRoot)
            throw "unexpected";
        
        href = url.substr(nprefix);
    }
    
    var ss = newBase.indexOf("://"); // assert true
    var slash = newBase.indexOf("/", ss + 3);
    if (slash != -1) // Trim to "http://....com:123".
        newBase = newBase.substr(0, slash);

    if (newBase.substr(-1) == "/")
        newBase = newBase.substr(0, newBase.length - 1);
    return newBase + href;
}

(function($) {

    var _ajax = $.ajax;
    $.ajax = function(params) {
        if ((typeof params) == 'string') {
            return _ajax(alterHref(params));
        }
        params.url = alterHref(params.url);
        return _ajax(params);
    };

    window.setLocationHref = function(href) {
        href = alterHref(href);
        window.location.href = href;
    };

})(jQuery);
