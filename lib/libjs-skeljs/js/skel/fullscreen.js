
import useragent from '../util/navigator/useragent';

export function fullScreen() {
    var hideTop = function() {
        $("#proj-info").slideUp({
            done: function() {
                $("#top").addClass("hide");
            }
        });
        $("#footbar").fadeOut();
    };

    var hTimeout;
    hTimeout = setTimeout(hideTop, 3000);

    $("#top").hover(function(e) {
        clearTimeout(hTimeout);
        hTimeout = null;

        $("#top").removeClass("hide");
        $("#proj-info").fadeIn();
        $("#footbar").fadeIn();
    }, function() {
        // TODO kill short-delay opener

        // enable auto-hide
        hTimeout = setTimeout(hideTop, 2000);
    });
}

if (userAgent.Chrome[0] > 600) {
    $("body").on('click', function() {
        document.documentElement.webkitRequestFullScreen();
        screen.orientation.lock("portrait");
    });
}
