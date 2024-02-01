
function mapAdd(map: any, key: string, val: any) {
    let v = map[key];
    if (v == null)
        map[key] = v = [];
    v.push(val);
    return v;
}

export function parseQueryString(s: string, decodeUri = true, parseQuotedString = false) {
    if (decodeUri) {
        s = s.replace(/\+/g, ' ');
        s = decodeURI(s);
    }
    let map: any = {};

    const S_NAME = 0;
    const S_VALUE = 1;
    const S_SINGLEQ = 2;
    const S_DOUBLEQ = 3;
    const S_Q_ESC = 4;
    const S_QQ_ESC = 5;
    const S_ESC_UNICODE = 6;
    type ParseState = number;

    let stack: ParseState[] = [];
    let state: ParseState = S_NAME;
    let push = (s: ParseState) => { stack.push(state); state = s };
    let pop = () => state = stack.pop()!;

    let buf = '';
    let key = '';
    let qbuf = '';
    let unicode = 0, unicodePos = 0;

    let len = s.length;
    for (let i = 0; i < len; i++) {
        let ch = s.charAt(i);
        switch (state) {
            case S_NAME:
            case S_VALUE:
                switch (ch) {
                    case '"':
                        if (parseQuotedString) {
                            push(S_DOUBLEQ); qbuf = ''; continue;
                        }
                        break;

                    case "'":
                        if (parseQuotedString) {
                            push(S_SINGLEQ); qbuf = ''; continue;
                        }
                        break;

                    case '=':
                        if (state == S_NAME) {
                            key = buf;
                            state = S_VALUE;
                            buf = '';
                            continue;
                        }
                        break;

                    case '&':
                        if (state == S_VALUE) {
                            mapAdd(map, key, buf);
                            state = S_NAME;
                            buf = '';
                        }
                        continue;
                }


                buf += ch;
                continue;

            case S_SINGLEQ:
                switch (ch) {
                    case "\\": push(S_Q_ESC); continue;
                    case "'":
                        buf += qbuf;
                        pop();
                        continue;
                }
                qbuf += (ch);
                continue;

            case S_DOUBLEQ:
                switch (ch) {
                    case "\\": push(S_QQ_ESC); continue;
                    case '"':
                        buf += qbuf;
                        pop();
                        continue;
                }
                qbuf += (ch);
                continue;

            case S_Q_ESC:
            case S_QQ_ESC:
                switch (ch) {
                    case 'r': ch = "\r"; break;
                    case 'n': ch = "\n"; break;
                    case 't': ch = "\t"; break;
                    case '0': ch = "\0"; break;
                    case 'u': unicode = 0; unicodePos = 0; push(S_ESC_UNICODE); continue;
                }
                qbuf += (ch);
                pop();
                continue;

            case S_ESC_UNICODE:
                let digit;
                let code = ch.charCodeAt(0);
                if (code >= 0x30 && code <= 0x39)
                    digit = code - 0x30;
                else if (code >= 0x41 && code <= 0x46) // A-F
                    digit = code - 0x41 + 10;
                else if (code >= 0x61 && code <= 0x66) // a-f
                    digit = code - 0x61 + 10;
                else
                    throw "parse error: invalid \\u (unicode escape) in quoted string: char '" + ch + "'";
                unicode = unicode * 0x10 + digit;
                if (unicodePos++ >= 4) {
                    let chr = String.fromCodePoint(unicode);
                    qbuf += (chr);
                    pop();
                }
                continue;
        }
    } // for

    if (state == S_VALUE)
        mapAdd(map, key, buf);

    return map;
}

export namespace QueryString {

    function parse(s: string, decodeUri = true) {
        return parseQueryString(s, decodeUri);
    }

}

(location as any).params = parseQueryString(location.search.substring(1));
