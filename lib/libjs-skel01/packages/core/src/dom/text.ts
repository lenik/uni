
export function getStyle(el: HTMLElement, styleProp: string) {
    var elm = el as any;
    if (elm.currentStyle)
        return elm.currentStyle[styleProp];
    if ((window as any).getComputedStyle) {
        let defaultView = document.defaultView;
        let pseudoElt = null;
        return (defaultView || window).getComputedStyle(elm, pseudoElt)
            .getPropertyValue(styleProp);
    }
    throw new Error('unexpected');
}

export function calculateLineHeight(el: HTMLElement) {
    let styleLineHeight = getStyle(el, 'line-height');
    var lineHeight = parseInt(styleLineHeight, 10);

    if (isNaN(lineHeight)) {
        let clone = el.cloneNode() as HTMLElement;
        let parent = el.parentElement!;

        clone.innerHTML = '<br>';
        parent.appendChild(clone);
        let singleLineHeight = clone.clientHeight;

        clone.innerHTML = '<br><br>';
        let doubleLineHeight = clone.clientHeight;
        parent.removeChild(clone);

        lineHeight = doubleLineHeight - singleLineHeight;
    }
    return lineHeight;
}

export function getLineCount(el: HTMLElement) {
    let lineHeight = calculateLineHeight(el);
    return Math.ceil(el.clientHeight / lineHeight - .1);
}

export function reWrap(el: HTMLElement) {
    let orig = el.textContent!;
    el.textContent = '';
    let tokens = orig.trim().split(/\s+/);
    let lines: string[] = [];
    let lineBuf = '';
    let lastH = -1;
    for (let i = 0; i < tokens.length; i++) {
        let token = tokens[i];

        if (i)
            el.insertAdjacentText('beforeend', ' ');
        el.insertAdjacentText('beforeend', token);

        if (lastH == -1)
            lastH = el.offsetHeight;
        else if (lastH != el.offsetHeight) {
            lines.push(lineBuf);
            lineBuf = '';
            lastH = el.offsetHeight;
        }

        if (lineBuf.length) lineBuf += ' ';
        lineBuf += token;
    }
    if (lineBuf.length)
        lines.push(lineBuf);
    el.textContent = orig;
    return lines;
}
