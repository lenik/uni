// require: jquery

(function($) {

    var input = $('<input class="inlineEdit" type="text" />');
    var csscopies = [
        "color", "border", "background",
        "font-family", "font-size", "font-style", "font-decoration", "font-weight",
        "opacity", "position",
        "text-align", "letter-spacing"
    ];

    function startInlineEdit(onblur, selection) {
        var $el = selection != null ? selection : this;
        input.val($el.text().trim());

        for (var i = 0; i < csscopies.length; i++) {
            var k = csscopies[i];
            var v = $el.css(k);
            input.css(k, v);
        }
        var width = $el.width();
        if (width < 32) width = 32;
        if (width > 800) width = 800;
        input.css("width", width + "px");
        input.css("max-width", $el.width() + 32 + "px");

        $el.hide();
        input.insertAfter($el);
        input.trigger('focus');

        var commit = function(e) {
            var text = input.val();
            $el.text(text);
            input.off();
            input.detach();
            $el.show();
            $el.text(text);
            if (onblur != null)
                onblur(e, text);
        };

        var hblur = input.on('blur', commit);
        var hkeydown = input.on('keydown', function(e) {
            // https://www.toptal.com/developers/keycode/table
            switch (e.key) {
            case 'Enter':
                commit(); break;
            case 'Escape':
                commit(); break; // cancel()?
            }
        });
    }

    $.fn.inlineEdit = function (onblur) {
        this.each(function(k, el) {
            var fn = function() { return startInlineEdit(onblur, $(el)); };
            if (el.inlineEdit_click != null)
                $(el).off("click", el.inlineEdit_click);
            el.inlineEdit_click = fn;
            $(el).on("click", fn);
        });
    };

})(jQuery);
