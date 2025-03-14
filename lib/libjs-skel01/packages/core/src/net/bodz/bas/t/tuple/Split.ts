import { char } from "skel01-core/src/lang/basetype";

function equalsIgnoreCase(a: string, b: string) {
    // if (a == null || b == null) return a === b;
    let al = a.toLowerCase();
    let bl = b.toLowerCase();
    return al == bl;
}

function nul() {
    return new Split(undefined, undefined, '/');
}

export type SplitNul = Split<undefined, undefined>;
export type SplitA = Split<string, string | undefined>;
export type SplitB = Split<string | undefined, string>;
export type SplitAB = Split<string, string>;
export type Split_ = Split<string | undefined, string | undefined>

export class Split<A, B> {

    a: A;
    b: B;
    sep: string;

    constructor(a: A, b: B, sep: char);
    constructor(a: A, b: B, sep: string) {
        this.a = a;
        this.b = b;
        this.sep = sep;
    }

    clone(): Split<A, B> {
        return new Split<A, B>(this.a, this.b, this.sep);
    }

    toString(): string {
        return `(${this.a}, ${this.b})`;
    }

    join(): string | undefined;
    join(sep: string): string | undefined;
    join(sep?: string, defaultVal?: string): string | undefined {
        let realSep = sep || this.sep;
        if (defaultVal == null) {
            if (this.a == null)
                return this.b as (string | undefined);
            if (this.b == null)
                return this.a as (string | undefined);
            return this.a + realSep + this.b;
        } else {
            let a = this.a || defaultVal;
            let b = this.b || defaultVal;
            return a + realSep + b;
        }
    }

    toSwapped(): Split<B, A> {
        return new Split<B, A>(this.b, this.a, this.sep);
    }

    get first(): A {
        return this.a;
    }
    set first(val: A) {
        this.a = val;
    }

    get second(): B {
        return this.b;
    }
    set second(val: B) {
        this.b = val;
    }

    define(): Split<A, B>;
    define(defaultVal: string = ""): Split<A, B> {
        if (this.a == null)
            this.a = defaultVal as A;
        if (this.b == null)
            this.b = defaultVal as B;
        return this;
    }

    trim(): Split<A, B> {
        if (this.a != null)
            this.a = (this.a as string).trim() as A;
        if (this.b != null)
            this.b = (this.b as string).trim() as B;
        return this;
    }

    removeEmpty(): Split<A, B> {
        if (this.a != null && (this.a as string).length == 0)
            this.a = undefined as A;
        if (this.b != null && (this.b as string).length == 0)
            this.b = undefined as B;
        return this;
    }

    removeBlank(): Split<A, B> {
        if (this.a != null && (this.a as string).trim().length == 0)
            this.a = undefined as A;
        if (this.b != null && (this.b as string).trim().length == 0)
            this.b = undefined as B;
        return this;
    }

    remove(s: string): Split<A, B>;
    remove(s: string, ignoreCase: boolean): Split<A, B>;
    remove(strings: string[]): Split<A, B>;
    remove(strings: string[], ignoreCase: boolean): Split<A, B>;

    remove(s: string | string[], ignoreCase = false): Split<A, B> {
        if (Array.isArray(s)) {
            let strings = s as string[];
            if (ignoreCase) {
                if (this.a != null)
                    for (let s of strings)
                        if (equalsIgnoreCase((this.a as string), s)) {
                            this.a = undefined as A;
                            break;
                        }
                if (this.b != null)
                    for (let s of strings)
                        if (equalsIgnoreCase((this.b as string), s)) {
                            this.b = undefined as B;
                            break;
                        }
            } else {
                if (this.a != null && strings.includes(this.a as string))
                    this.a = undefined as A;
                if (this.b != null && strings.includes(this.b as string))
                    this.b = undefined as B;
            }
        } else {
            if (ignoreCase) {
                if (this.a != null && equalsIgnoreCase(this.a as string, s))
                    this.a = undefined as A;
                if (this.b != null && equalsIgnoreCase(this.b as string, s))
                    this.b = undefined as B;
            } else {
                if (this.a != null && this.a == s)
                    this.a = undefined as A;
                if (this.b != null && this.b == s)
                    this.b = undefined as B;
            }
        }
        return this;
    }

    /**
     * convert `from` to `to`, in both a/b fields.
     */
    convert(from: string, to: string): Split<A, B> {
        if (from == this.a)
            this.a = to as A;
        if (from == this.b)
            this.b = to as B;
        return this;
    }

    static shift(s: string, sep: char): SplitA;
    static shift(s: undefined | null, sep: char): SplitNul;
    static shift(s: string | undefined | null, sep: char): SplitA | SplitNul;
    static shift(s: string | undefined | null, sep: char): SplitA | SplitNul {
        if (s == null)
            return nul();
        let pos = s.indexOf(sep);
        if (pos == -1)
            return new Split(s, undefined, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + 1), sep);
    }

    static shiftFirst(s: string, sep?: string): SplitA;
    static shiftFirst(s: undefined | null, sep?: string): SplitNul;
    static shiftFirst(s: string | undefined | null, sep?: string): SplitA | SplitNul;
    static shiftFirst(s: string | undefined | null, sep?: string): SplitA | SplitNul {
        if (s == null)
            return nul();
        if (sep == null)
            throw new Error("sep is null");
        if (sep.length == 0)
            throw new Error("empty sep");
        let pos = s.indexOf(sep);
        if (pos == -1)
            return new Split(s, undefined, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + sep.length), sep);
    }

    static skip(s: string, sep: char): SplitB;
    static skip(s: undefined | null, sep: char): SplitNul;
    static skip(s: string | undefined | null, sep: char): SplitB | SplitNul;
    static skip(s: string | undefined | null, sep: char): SplitB | SplitNul {
        if (s == null)
            return nul();
        let pos = s.indexOf(sep);
        if (pos == -1)
            return new Split(undefined, s, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + 1), sep);
    }

    static skipFirst(s: string, sep?: string): SplitB;
    static skipFirst(s: undefined | null, sep?: string): SplitNul;
    static skipFirst(s: string | undefined | null, sep?: string): SplitB | SplitNul;
    static skipFirst(s: string | undefined | null, sep?: string): SplitB | SplitNul {
        if (s == null)
            return nul();
        if (sep == null)
            throw new Error("sep is null");
        if (sep.length == 0)
            throw new Error("empty sep");
        let pos = s.indexOf(sep);
        if (pos == -1)
            return new Split(undefined, s, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + sep.length), sep);
    }

    static pop(s: string, sep: char): SplitB;
    static pop(s: undefined | null, sep: char): SplitNul;
    static pop(s: string | undefined | null, sep: char): SplitB | SplitNul;
    static pop(s: string | undefined | null, sep: char): SplitB | SplitNul {
        if (s == null)
            return nul();
        let pos = s.lastIndexOf(sep);
        if (pos == -1)
            return new Split(undefined, s, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + 1), sep);
    }

    static popLast(s: string, sep?: string): SplitB;
    static popLast(s: undefined | null, sep?: string): SplitNul;
    static popLast(s: string | undefined | null, sep?: string): SplitB | SplitNul;
    static popLast(s: string | undefined | null, sep?: string): SplitB | SplitNul {
        if (s == null)
            return nul();
        if (sep == null)
            throw new Error("null sep");
        if (sep.length == 0)
            throw new Error("empty sep");
        let pos = s.lastIndexOf(sep);
        if (pos == -1)
            return new Split(undefined, s, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + sep.length), sep);
    }

    static chop(s: string, sep: char): SplitA;
    static chop(s: undefined | null, sep: char): SplitNul;
    static chop(s: string | undefined | null, sep: char): SplitA | SplitNul;
    static chop(s: string | undefined | null, sep: char): SplitA | SplitNul {
        if (s == null)
            return nul();
        let pos = s.lastIndexOf(sep);
        if (pos == -1)
            return new Split(s, undefined, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + 1), sep);
    }

    static chopLast(s: string, sep?: string): SplitA;
    static chopLast(s: undefined | null, sep?: string): SplitNul;
    static chopLast(s: string | undefined | null, sep?: string): SplitA | SplitNul;
    static chopLast(s: string | undefined | null, sep?: string): SplitA | SplitNul {
        if (s == null)
            return nul();
        if (sep == null)
            throw new Error("null sep");
        if (sep.length == 0)
            throw new Error("empty sep");
        let pos = s.lastIndexOf(sep);
        if (pos == -1)
            return new Split(s, undefined, sep);
        else
            return new Split(s.substring(0, pos), s.substring(pos + sep.length), sep);
    }

    static keyValue(s: string): SplitA;
    static keyValue(s: undefined | null): SplitNul;
    static keyValue(s: string | undefined | null): SplitA | SplitNul;

    static keyValue(s: string, sep: char): SplitA;
    static keyValue(s: undefined | null, sep: char): SplitNul;
    static keyValue(s: string | undefined | null, sep: char): SplitA | SplitNul;

    static keyValue(s: string | undefined | null, sep: char = '='): SplitA | SplitNul {
        return this.shift(s, sep);
    }

    static dirBase(s: string): SplitB;
    static dirBase(s: undefined | null): SplitNul;
    static dirBase(s: string | undefined | null): SplitB | SplitNul;
    static dirBase(s: string | undefined | null): SplitB | SplitNul {
        return this.popLast(s, '/');
    }

    static nameExtension(s: string): SplitA;
    static nameExtension(s: undefined | null): SplitNul;
    static nameExtension(s: string | undefined | null): SplitA | SplitNul;
    static nameExtension(s: string | undefined | null): SplitA | SplitNul {
        return this.chop(s, '.');
    }

    static pathQuery(s: string): SplitA;
    static pathQuery(s: undefined | null): SplitNul;
    static pathQuery(s: string | undefined | null): SplitA | SplitNul;
    static pathQuery(s: string | undefined | null): SplitA | SplitNul {
        return this.shift(s, '?');
    }

    static hostPort(s: string): SplitA;
    static hostPort(s: undefined | null): SplitNul;
    static hostPort(s: string | undefined | null): SplitA | SplitNul;
    static hostPort(s: string | undefined | null): SplitA | SplitNul {
        return this.chop(s, ':');
    }

    static protocolOther(s: string): SplitB;
    static protocolOther(s: undefined | null): SplitNul;
    static protocolOther(s: string | undefined | null): SplitB | SplitNul;
    static protocolOther(s: string | undefined | null): SplitB | SplitNul {
        return this.skipFirst(s, "://");
    }

    static packageName(s: string): SplitB;
    static packageName(s: undefined | null): SplitNul;
    static packageName(s: string | undefined | null): SplitB | SplitNul;
    static packageName(s: string | undefined | null): SplitB | SplitNul {
        return this.pop(s, '.');
    }

    static headDomain(s: string): SplitA;
    static headDomain(s: undefined | null): SplitNul;
    static headDomain(s: string | undefined | null): SplitA | SplitNul;
    static headDomain(s: string | undefined | null): SplitA | SplitNul {
        return this.shift(s, '.');
    }

    static majorVersion(s: string): SplitA;
    static majorVersion(s: undefined | null): SplitNul;
    static majorVersion(s: string | undefined | null): SplitA | SplitNul;
    static majorVersion(s: string | undefined | null): SplitA | SplitNul {
        return this.shift(s, '.');
    }

    static artifactClassifier(fileNameWoExt: string, version?: string): SplitA;
    static artifactClassifier(fileNameWoExt: undefined | null, version?: string): SplitNul;
    static artifactClassifier(fileNameWoExt: string | undefined | null, version?: string): SplitA | SplitNul;
    static artifactClassifier(fileNameWoExt: string | undefined | null, version?: string): SplitA | SplitNul {
        let split = this.chopLast(fileNameWoExt, "-" + version);
        if (split.b != null && split.b.startsWith("-"))
            split.b = split.b.substring(1);
        return split;
    }

}

export default Split;
