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

    moment.locale("zh-cn");

    $("#cal1").clndr({
        template: $('#templ').html(),
        clickEvents: {
            click: function (target) {
                console.log('Cal-2 clicked: ', target);
            },
            nextInterval: function () {
                console.log('Cal-2 next interval');
            },
            previousInterval: function () {
                console.log('Cal-2 previous interval');
            },
            onIntervalChange: function () {
                console.log('Cal-2 interval changed');
            }
        }
    });

});
