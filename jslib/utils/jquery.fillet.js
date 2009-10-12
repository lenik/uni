
(function ($) {

$.fn.fillet4 = function(img, hTop, wLeft, hBottom, wRight) {
    if (undefined == hTop) throw "at least height isn't specified";
    if (undefined == wLeft) wLeft = hTop;
    if (undefined == hBottom) hBottom = hTop;
    if (undefined == wRight) wRight = wLeft;
    return this.each(function() {
        var $this = $(this);
        if ($this.css("position") == 'static')
            $this.css("position", "relative");
        $this.append($("<div/>").css({
            'position'  : 'absolute',
            'display'   : 'block',
            'left'      : '0px',
            'top'       : '0px',
            'background': 'url('+img+') no-repeat top left',
            'width'     : wLeft,
            'height'    : hTop,
            }));
        $this.append($("<div/>").css({
            'position'  : 'absolute',
            'display'   : 'block',
            'right'     : '0px',
            'top'       : '0px',
            'background': 'url('+img+') no-repeat top right',
            'width'     : wRight,
            'height'    : hTop,
            }));
        $this.append($("<div/>").css({
            'position'  : 'absolute',
            'display'   : 'block',
            'left'      : '0px',
            'bottom'    : '0px',
            'background': 'url('+img+') no-repeat bottom left',
            'width'     : wLeft,
            'height'    : hBottom,
            }));
        $this.append($("<div/>").css({
            'position'  : 'absolute',
            'display'   : 'block',
            'right'     : '0px',
            'bottom'    : '0px',
            'background': 'url('+img+') no-repeat bottom right',
            'width'     : wRight,
            'height'    : hBottom,
            }));
    });
};

})(jQuery);
