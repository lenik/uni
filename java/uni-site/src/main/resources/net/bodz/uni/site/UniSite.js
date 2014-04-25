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

});