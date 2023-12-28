
function make(el, opts) {
    var $sel = $(el);

    // iscroll only supports single-child, so wrap multiple children if necessary.
    if ($sel.children().length > 1) {
        var $wrapper = $(".iscroll-wrapper", $sel);
        if ($wrapper.length == 0) {
            var div = document.createElement("div");
            $wrapper = $(div);
            $wrapper.addClass("iscroll-wrapper");

            $wrapper.append($sel.children());
            $sel.append($wrapper);
        }
    }

    if (opts == null)
        opts = {
            click: true,
            mouseWheel: true,
            keyBindings: true
        };

    // TODO document.ready.. not work..
    new IScroll(el, opts);
}

if (window.IScroll != null) {
    $.fn.iscroll = function (opts) {
        this.each(function (i, el) {
            make(el, opts);
        });
    };

    $(".iscroll").iscroll();
}
