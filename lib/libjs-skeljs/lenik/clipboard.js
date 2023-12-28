window.Clipboard = {

    set: function(s) {
        var textArea = document.createElement("textarea");
        textArea.value = s;

        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');

        document.body.removeChild(textArea);
    }

};
