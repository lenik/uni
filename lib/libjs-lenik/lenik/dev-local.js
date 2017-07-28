/**
 * Replace href with the given base.
 *
 * @param href
 * @param newBase should be absolute url, like `http://...`
 */
function alterHrefBase(href, newBase) {
    if (location.protocol != "file:")
        return href;

    if (newBase == null || newBase == '')
        return href;

    if (href.indexOf(":") != -1)
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
            return _ajax(alterHrefBase(params, window.hrefOverride));
        }
        params.url = alterHrefBase(params.url, window.hrefOverride);
        return _ajax(params);
    };

    window.setLocationHref = function(href) {
        href = alterHrefBase(href, window.hrefOverride);
        window.location.href = href;
    };

})(jQuery);