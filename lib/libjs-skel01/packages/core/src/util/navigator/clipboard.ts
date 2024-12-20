
export function setClipboardText(s) {
    navigator.clipboard.writeText(s);
}

export function _setClipboardText(s) {
    var textArea = document.createElement("textarea");
    textArea.value = s;

    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');

    document.body.removeChild(textArea);
}

window.Clipboard = {

    set: _setClipboardText

};
