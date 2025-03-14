
export namespace Character {

    export function isWhitespace(ch: string): boolean {
        const code = ch.charCodeAt(0);
        switch (code) {
            case 0x20: // SP
            case 0x09: // \t
            case 0x0A:  // \n
            case 0x0D: // \r
                return true;

            case 0x0B: // \v ^K vertical tab
            case 0x0C: // \f  ^L form feed
                return true;

            case 0x00a0:
            case 0x1680:
            case 0x2000:
            case 0x200a:
            case 0x2028:
            case 0x2029:
            case 0x202f:
            case 0x205f:
            case 0x3000:
            case 0xfeff:
                return true;
            default:
                return false;
        }
    }

    export function isLetter(ch: string): boolean {
        return isLatinLetter(ch);
    }

    export function isLatinLetter(ch: string): boolean {
        if (ch.toUpperCase() != ch.toLowerCase())
            return true;
        return false;
    }

    export function isAnyLetter(ch: string): boolean {
        if (ch.toUpperCase() != ch.toLowerCase())
            return true;
        if (ch.codePointAt(0)! > 127)
            return true;
        return false;
    }

    export function toLowerCase(ch: string) {
        return ch.toLowerCase();
    }

    export function toUpperCase(ch: string) {
        return ch.toUpperCase();
    }

}

export default Character;
