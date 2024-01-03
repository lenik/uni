
export function parseUserAgent(s: string) {
    // 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
    s = s.replace(/\(.*?\)/g, ''); // remove comments
    
    // 'Mozilla/5.0  AppleWebKit/537.36  Chrome/120.0.0.0 Safari/537.36'
    var prefix = '';
    var map: any = {};
    s.split(/\s+/).forEach(function (group) {
        var slash = group.indexOf('/');
        if (slash == -1)
            prefix = group + " ";
        else {
            let name = prefix + group.substring(0, slash);
            let verArray = group.substring(slash + 1).split(/\./).map(a => a * 1);
            map[name] = verArray;
            prefix = "";
        }
    });
    return $.extend({ Chrome: [ 0 ], Foo: [ 0 ] }, map);
}

export var userAgent = parseUserAgent(navigator.userAgent);

var win: any = window;
win.userAgent = userAgent;
