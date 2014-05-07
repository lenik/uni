window.clickHandler = {};

(function($) {

    $(document).ready(function() {

        var speed = 'medium';

        function menubox(elm) {
            var menu = $(elm).parents(".ui-menu");
            var menubox = menu.find(".ui-menubox");
            return menubox;
        }

        $(".ui-menu").hover(function(event) {
            menubox(event.target).fadeIn(speed);
        }, function(event) {
            menubox(event.target).fadeOut(speed);
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
                    handler(value);
                } else {
                    location.reload();
                }
            });
        });

    });

    window.clickHandler["toolMenu.theme"] = function(val) {
        var themeLink = $("#themeLink")[0];
        themeLink.href = _webApp_ + "theme-" + val + ".css";
    };

    window.reloadSite = function() {
        $.get(_webApp_ + "reload", null, location.reload);
    };

})(jQuery);
