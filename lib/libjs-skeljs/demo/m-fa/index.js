$(document).ready(function() {

    $(document.body).keydown(function (e) {
        var el = $(e.target);
        if (el.hasClass("editable")) return;
        if (el.is("input, select")) return;

        switch (e.keyCode) {
        case 27: // ESC
        case 32: // SPC
        case 46: // DEL
            return;
        }
    });

    // fullScreen();

});
