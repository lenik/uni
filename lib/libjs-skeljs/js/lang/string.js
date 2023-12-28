
export function startsWith(s, head) {
    if (head == null)
        return false;
    
        var headLen = head.length;
    if (headLen > s.length)
        return false;

    return s.substring(0, headLen) == head;
}

export function endsWith(s, tail) {
    if (tail.length > s.length)
        return false;
    return s.substring(s.length - tail.length) == tail;
}

if (String.prototype.startsWith == undefined)
    String.prototype.startsWith = (head) => startsWith(this, head);

if (String.prototype.endsWith == undefined)
    String.prototype.endsWith = (tail) => endsWith(this, tail);
