window.clickHandler = {};

(function($) {

    $(document).ready(function() {

        var speed = 'medium';

        function menubox(elm) {
            var menu = $(elm).parents(".ui-menu");
            var menubox = menu.find(".ui-menubox");
            return menubox;
        }

        $(".ui-menu").click(function(event) {
            menubox(event.target).fadeIn(speed);
        });

        $(".ui-menu").hover(function(event) {
            menubox(event.target).stop().fadeToggle(speed);
        }, function(event) {
            menubox(event.target).stop().fadeOut(speed);
        });

        $(".ui-enum li a").click(function(event) {
            var a = $(event.target);
            var ul = a.parents(".ui-enum");
            var id = ul.attr("id");
            ul.find("li").removeClass("ui-active");
            a.parent("li").addClass("ui-active");

            var path = ul.attr("path");
            var value = a.attr("value");
            $.get(_webApp_ + path + "/" + value, null, function() {
                var handler = window.clickHandler[id];
                if (handler != null) {
                    handler(a, value);
                } else {
                    location.reload();
                }
            });
        });

    });

    window.clickHandler["toolMenu.theme"] = function(a, val) {
        var themeLink = $("#themeLink")[0];
        themeLink.href = _webApp_ + "theme-" + val + ".css";
    };

    window.clickHandler["toolMenu.language"] = function(a) {
        var href = location.href;
        var m = href.match(/\/intl\/([A-Za-z-]+)/);
        if (m == null) {
            location.reload();
        } else {
            var code = a.attr("code");
            location.href = href.replace(/\/intl\/[A-Za-z-]+/, "/intl/" + code);
        }
    };

    window.reloadSite = function() {
        $.get(_webApp_ + "reload", null, location.reload);
    };

})(jQuery);
