(function($) {

    $(document).ready(function() {

        var speed = 'medium';

        function menubox(elm) {
            var menu = $(elm).parents(".ui-menu");
            var menubox = menu.find(".ui-menubox");
            return menubox;
        }

        $(".ui-menu").hover(function(event) {
            menubox(event.srcElement).fadeIn(speed);
        }, function(event) {
            menubox(event.srcElement).fadeOut(speed);
        });

        $(".ui-enums li a").click(function(event) {
            var a = event.target;
            $(a).parents(".ui-enums").find("li").removeClass("ui-active");
            $(a).parent("li").addClass("ui-active");
        });

    });

    window.setTheme = function(theme, suffix) {
        var themeLink = $("#themeLink")[0];
        themeLink.href = _webApp_ + "theme-" + suffix + ".css";
        $.get(_webApp_ + "preferences/setTheme/" + theme);
    };

    window.setLanguage = function(lang) {
        $.get(_webApp_ + "preferences/setLanguage/" + lang);
        location.reload();
    };

    window.reloadSite = function() {
        $.get(_webApp_ + "reload");
        location.reload();
    };

})(jQuery);
