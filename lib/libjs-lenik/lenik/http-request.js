
function parseQueryString(s, ctx) {
    if (s == null)
        return null;
    if (s.length == 0)
        return {};
    var exp = decodeURI(s)
        .replace(/"/g, '\\"')
        .replace(/&/g, '","')
        .replace(/=/g, '":"');
    var obj = JSON.parse('{"' + exp + '"}');
    if (ctx == null)
        return obj;
    else
        return $.extend(ctx, obj);
}

function VarMap(s) {
    this.parseURI = function(s) {
        var ques = s.indexOf("?");
        s = s.substr(ques + 1);
        parseQueryString(s, this);
    };

    this.parseQueryString = function(s) {
        parseQueryString(s, this);
    };

    this.applyTo = function(str) {
        var re = /\$(\w+)/g;
        var group;
        var last = 0;
        var parts = [];
        while (group = re.exec(str)) {
            var k = group[1];
            var content = this[k];
            parts.push(str.substr(last, group.index - last));
            parts.push(content);
            last = group.index + group[0].length;
        }
        parts.push(str.substr(last));
        return parts.join('');
    };

    if (s != null)
        this.parseQueryString(s);
}

Location.prototype.params = new VarMap(location.search.substr(1));
