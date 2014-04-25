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
            //menubox(event.srcElement).fadeOut(speed);
        });

    });

})(jQuery);

function setTheme(theme, a) {
    themeLink.href = _webApp_ + "UniSite-" + theme + ".css";
    $(a).parents(".ui-enums").find("li").removeClass("ui-active");
    $(a).parent("li").addClass("ui-active");
}
