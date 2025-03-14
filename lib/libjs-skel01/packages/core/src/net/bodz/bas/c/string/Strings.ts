import { char, int } from "../../../../../lang/basetype";
import Character from "../../../../../lang/Character";
import PosRange from "./PosRange";

const ellipsisNull: string | null = null;
const PADCHAR = ' ';

export class Strings {

    static repeat(count: int, pattern: string): string {
        let buf = '';
        while (--count >= 0)
            buf += pattern;
        return buf;
    }

    static reverse(s?: string): string | null | undefined {
        if (s == null) return s;
        return [...s].reverse().join('');
    }

    static padLeft(s: string, len: int): string;
    static padLeft(s: string, len: int, padChar?: char): string {
        let padLen = len - s.length;
        if (padLen <= 0)
            return s;
        return Strings.repeat(padLen, padChar || PADCHAR) + s;
    }

    static padMiddle(s: string, len: int): string;
    static padMiddle(s: string, len: int, padChar?: char): string {
        let padLen = len - s.length;
        if (padLen <= 0)
            return s;
        let padLeft = padLen / 2; // slightly left-ward
        let padRight = padLen - padLeft;
        let pad = padChar || PADCHAR;
        return Strings.repeat(padLeft, pad) + s + Strings.repeat(padRight, pad);
    }

    static padRight(s: string, len: int): string;
    static padRight(s: string, len: int, padChar?: char): string {
        let padLen = len - s.length;
        if (padLen <= 0)
            return s;
        return s + Strings.repeat(padLen, padChar || PADCHAR);
    }

    static _lcfirst(s: string | undefined | null): string | undefined | null {
        if (s == null)
            return s;
        if (s.length <= 1)
            return s.toLowerCase();
        let lcfirst = s.charAt(0).toLowerCase();
        return lcfirst + s.substring(1);
    }

    static lcfirst(s: string | undefined | null): string | undefined | null {
        if (s == null)
            return s;
        switch (s.length) {
            case 0:
                return s;

            case 1:
                return s.toLowerCase();
        }

        let lcfirst = s.charAt(0).toLowerCase();
        return lcfirst + s.substring(1);
    }

    static ucfirst(s: string | undefined | null): string | undefined | null {
        if (s == null)
            return s;
        if (s.length <= 1)
            return s.toUpperCase();
        let ucfirst = s.charAt(0).toUpperCase();
        return ucfirst + s.substring(1);
    }

    static ucfirstWords(s: string | undefined | null): string | undefined | null {
        if (s == null)
            return s;
        let boundary = true;
        let len = s.length;
        let buf = '';
        for (let i = 0; i < len; i++) {
            let c = s.charAt(i);
            let isLetter = Character.isLetter(c);
            if (boundary && isLetter)
                c = c.toUpperCase();
            boundary = !isLetter;
            buf += c;
        }
        return buf;
    }

    static ellipsis(s: string | undefined | null, len: int): string | undefined | null;
    static ellipsis(s: string | undefined | null, len: int, ellipse?: string): string | undefined | null;
    static ellipsis(s: string | undefined | null, len: int, ellipse?: string, epstart?: string, epend?: string): string | undefined | null {
        if (s == null)
            return s;
        ellipse ||= '...';
        if (epstart == null) {
            if (s.length <= len)
                return s;
            let elen = ellipse.length;
            if (len > elen)
                return s.substring(0, len - elen) + ellipse;
            else
                return ellipse.substring(0, len);
        } else {
            // assert epend!=null;
            if (s == null)
                return ellipsisNull;
            let estart = 0;
            if (epstart != null)
                if ((estart = s.indexOf(epstart)) == -1)
                    estart = 0;
                else
                    estart += epstart.length;
            let eend = s.length;
            if (epend != null)
                if ((eend = s.lastIndexOf(epend)) == -1)
                    eend = s.length;
            let trims = estart + (s.length - eend);
            let prefix = s.substring(0, estart);
            let suffix = s.substring(eend);
            if (len > trims + ellipse.length)
                return prefix + this.ellipsis(s.substring(estart, eend), len - trims, ellipse) + suffix;
            else
                return this.ellipsis(s, len, ellipse);
        }
    }

    // surrogate-pair not supported.
    static indexOf(s: string, ch: char, indexOfOccurence: int): int {
        let begin = 0;
        let end = s.length;
        let index = 0;
        while (begin < end) {
            let pos = s.indexOf(ch, begin);
            if (pos == -1)
                return -1;
            if (index++ == indexOfOccurence)
                return pos;
            begin = pos + 1;
        }
        return -1;
    }

    static selectToken(s: string, tokenIndex: int, delim: char = '/'): PosRange {
        if (tokenIndex < 0)
            throw new Error("tokenIndex is out of index: " + tokenIndex);

        let currentIndex = 0;
        let begin = 0;
        let len = s.length;

        while (true) {
            if (begin > len)
                throw new Error("tokenIndex is out of index: " + tokenIndex);

            let end = s.indexOf(delim, begin);
            if (end == -1)
                end = s.length;

            if (currentIndex == tokenIndex)
                return new PosRange(begin, end);

            begin = end + 1;
            currentIndex++;
        }
    }

    static leadingSpace(s: string): string {
        let end = s.length;
        while (end > 0) {
            let ch = s.charAt(end - 1);
            if (ch == '\n' || ch == '\r')
                end--;
            else
                break;
        }
        for (let i = 0; i < end; i++) {
            let ch = s.charAt(i);
            if (!Character.isWhitespace(ch))
                return s.substring(0, i);
        }
        return s.substring(0, end);
    }

    static trailingSpace(s: string): string {
        let end = s.length;
        while (end > 0) {
            let ch = s.charAt(end - 1);
            if (ch == '\n' || ch == '\r')
                end--;
            else
                break;
        }
        for (let i = end - 1; i >= 0; i++) {
            let ch = s.charAt(i);
            if (!Character.isWhitespace(ch))
                return s.substring(i + 1, end);
        }
        return s.substring(0, end);
    }

}

export default Strings;
