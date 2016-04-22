$(document).ready(function() {
    $('.ZwkMenu').addClass('-sub');

    var topMenus = $('.ZwkMenuBar > li > .ZwkMenu').addClass('-top').removeClass('-sub');
    topMenus.each(function(i, mnu) {
        var btn = $(mnu).parent('li');
        btn.mouseenter(function(e) {

            $('.ZwkMenu').removeClass('selected').stop(true, true).fadeOut();
            $(mnu).addClass('selected').stop(true, true).fadeIn();

            btn.siblings().removeClass('selected');
            btn.addClass('selected');

            $(mnu).position({
                my: 'left top',
                at: 'left bottom',
                of: btn,
                collision: 'fit'
            });
        });

        $(mnu).mouseleave(function(e) {
            $(mnu).stop(true, true).fadeOut();
        });
    });

    topMenus.insertAfter('.ZwkMenuBar');

    var subMenus = $('.ZwkMenu.-sub');
    subMenus.each(function(i, mnu) {
        var btn = $(mnu).parent('li');
        btn.mouseenter(function(e) {

            btn.children('.ZwkMenu').removeClass('selected').stop(true, true).fadeOut();
            $(mnu).addClass('selected').stop(true, true).fadeIn();

            btn.siblings().removeClass('selected');
            btn.addClass('selected');

            $(mnu).position({
                my: 'left top',
                at: 'right top',
                of: btn,
                collision: 'fit'
            });
        });

        btn.mouseleave(function(e) {
            $(mnu).stop(true, true).fadeOut();
        });
    });

    $('.ZwkMenu a').click(function() {
        $('.ZwkMenu').stop(true, true).fadeOut();
    });
});
