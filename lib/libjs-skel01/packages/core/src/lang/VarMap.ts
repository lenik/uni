import { parseQueryString } from "../cgi/QueryString"

interface INameMap {

    get(name: string): string | undefined

    getArray(name: string): string[]

    set(name: string, value: string): void

    setArray(name: string, values: string[]): void

    add(name: string, value: string): string[]

}

interface INamedStrings {

    [name: string]: string[]

}

export class VarMap implements INameMap {

    map: INamedStrings = {}

    constructor(map?: any) {
        if (map != null)
            for (let k in map) {
                let val = map[k];
                if (Array.isArray(val))
                    this.map[k] = val as string[]; // maybe other than string[]?
                else
                    this.map[k] = [val];
            }
    }

    applyTo(str: string, nullText = "") {
        let re = /\$(\w+)/g;
        let group: any;
        let last = 0;
        let parts: string[] = [];
        while (group = re.exec(str)) {
            let k = group[1];
            let content = this.get(k);
            parts.push(str.substring(last, group.index - last));
            if (content != null)
                parts.push(content);
            else
                parts.push(nullText);
            last = group.index + group[0].length;
        }
        parts.push(str.substring(last));
        return parts.join('');
    }

    get empty() {
        return Object.keys(this.map).length == 0;
    }

    get keySet() {
        return Object.keys(this.map);
    }

    get names() {
        return Object.keys(this.map);
    }

    get(name: string): string | undefined {
        let array = this.map[name];
        return array == null ? undefined : array[0];
    }

    getArray(name: string): string[] {
        return this.map[name];
    }

    set(name: string, value: string) {
        this.map[name] = [value];
    }

    setArray(name: string, values: string[]) {
        this.map[name] = values;
    }

    add(name: string, value: string) {
        let v = this.map[name];
        if (v == null) this.map[name] = v = [];
        v.push(value);
        return v;
    }

    get queryString() {
        let qs = "";
        for (let k in this.map) {
            let v = this.map[k];
            for (let item of v) {
                let encoded = encodeURI(item);
                if (qs.length) qs += '&';
                qs += k + '=' + encoded;
            }
        }
        return qs;
    }

    static parse(queryString: string, decodeUri = true) {
        let varMap = new VarMap();
        let map = parseQueryString(queryString, decodeUri);
        varMap.map = map;
    }

    static fromURI(s: string) {
        let ques = s.indexOf("?");
        let qs =
            ques == -1 ? '' : s.substring(ques + 1);
        return parseQueryString(s, true);
    }

}
