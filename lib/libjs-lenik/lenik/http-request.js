
export class QueryString {
    
    parse(s) {
        if (s == null)
            return null;
        if (s.length == 0)
            return {};
        var exp = decodeURI(s)
            .replace(/"/g, '\\"')
            .replace(/&/g, '","')
            .replace(/=/g, '":"');
        var obj = JSON.parse('{"' + exp + '"}');
        return obj;
    }
    
    parseURI(s) {
        var ques = s.indexOf("?");
        s = s.substr(ques + 1);
        parseQueryString(s, this);
    }

    applyTo(str) {
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
    }
}

location.params = QueryString.parse(location.search.substring(1));
