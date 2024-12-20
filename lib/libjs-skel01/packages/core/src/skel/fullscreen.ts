
import { userAgent } from '../util/navigator/useragent';

export function fullScreen() {
    var hideTop = function() {
        $("#proj-info").slideUp({
            done: function() {
                $("#top").addClass("hide");
            }
        });
        $("#footbar").fadeOut();
    };

    var hTimeout = setTimeout(hideTop, 3000);

    $("#top").on('hover', function(e) {
        clearTimeout(hTimeout);

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
        (document.documentElement as any).webkitRequestFullScreen();
        (screen.orientation as any).lock("portrait");
    });
}
