$(document).ready(function() {

    // TODO onPageEnter/Leave events

    function turnPage(delta) {
        var pages = $(".page");
        var fromPage = $(".page.selected");
        var from = pages.index(fromPage);
        var to = (from + delta + pages.length) % pages.length;
        var toPage = $(pages[to]);
        toPage.css('left', '10%');
        toPage.css('z-index', 5);
        fromPage.css('z-index', 10);
        if (from != -1) {
            fromPage.animate({
                left: delta < 0 ? '100%' : '-100%'
            }, 300, 'easeInExpo', function() {
                fromPage.removeClass('selected'); 
            });
            toPage.fadeIn(300, function() {
                toPage.addClass("selected");
            });
        } else {
            toPage.fadeIn(300, function() {
                toPage.addClass("selected");
            });
        }
    }
    
    $(".nav > .prev").click(function(e) {
        turnPage(-1);
    });
    
    $(".nav > .next").click(function(e) {
        turnPage(1);
    });

});

