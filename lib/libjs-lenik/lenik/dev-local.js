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

function alterHref(href) {
    return alterHrefBase(href, window.hrefOverride);
}

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

    if (href.charAt(0) == "/") {
        var ss = newBase.indexOf("://"); // assert true
        var slash = newBase.indexOf("/", ss + 3);
        if (slash != -1) // Trim to "http://....com:123".
            newBase = newBase.substr(0, slash);
        return newBase + href;
    }

    // href is relative-path.
    if (newBase.substr(-1) != "/")
        newBase += "/";
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
