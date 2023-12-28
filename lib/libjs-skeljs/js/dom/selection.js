(function ($) {

    /**
     * If <element> has @onenter or @onleave, they will be called respectively.
     */
    $.fn.selectNext = function(delta) {
        if (delta == 0)
            return true;
        if (delta == null) delta = 1;

        var next;
        if (delta > 0) {
            next = this.next();
            if (next.length == 0)
                return false;
            next = $(next[delta - 1]);
        } else {
            next = this.prev();
            if (next.length == 0)
                return false;
            next = $(next[-delta - 1]);
        }

        var onleave = this.attr('onleave');
        if (onleave != null) {
            var event = { target: this };
            eval(onleave);
        }

        if (this.hasClass('selected')) {
            this.removeClass('selected');
            next.addClass('selected');
        } else {
            this.hide();
            next.show();
        }

        var onenter = next.attr('onenter');
        if (onenter != null) {
            var event = { target: next };
            eval(onenter);
        }
        return true;
    };

})(jQuery);
