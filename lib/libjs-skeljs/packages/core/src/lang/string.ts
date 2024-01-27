
export function startsWith(s: string, head?: string): boolean {
    if (head == null)
        return false;

    var headLen = head.length;
    if (headLen > s.length)
        return false;

    return s.substring(0, headLen) == head;
}

export function endsWith(s: string, tail?: string): boolean {
    if (tail == null)
        return false;
    if (tail.length > s.length)
        return false;
    return s.substring(s.length - tail.length) == tail;
}

if (String.prototype.startsWith == undefined)
    String.prototype.startsWith = (head) => startsWith(this as unknown as string, head);

if (String.prototype.endsWith == undefined)
    String.prototype.endsWith = (tail) => endsWith(this as unknown as string, tail);

export function replaceLiteralOrRegex(s: string, pattern: string, replacement: string) {
    if (pattern.charAt(0) == "/") {
        pattern = pattern.substring(1);
        let lastSlash = pattern.lastIndexOf("/");
        let mode = pattern.substring(lastSlash + 1);
        pattern = pattern.substring(0, lastSlash);
        let regex = new RegExp(pattern, mode);
        return s.replace(regex, replacement);
    } else {
        return s.replace(pattern, replacement);
    }
}
