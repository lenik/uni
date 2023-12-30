
export function parseUserAgent(s) {
    s = s.replace(/\(.*?\)/g, ''); // remove comments
    var prefix = '';
    var map = {};
    s.split(/\s+/).forEach(function (g) {
        var slash = g.indexOf('/');
        if (slash == -1)
            prefix = g + " ";
        else {
            map[prefix + g.substr(0, slash)]
                = g.substr(slash + 1).split(/\./).map(
                    function(a) { return a*1; });
            prefix = "";
        }
    });
    return $.extend({ Chrome: [ 0 ], Foo: [ 0 ] }, map);
}

window.userAgent = parseUserAgent(navigator.userAgent);
