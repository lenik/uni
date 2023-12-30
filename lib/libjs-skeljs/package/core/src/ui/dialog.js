
export function showDialog(dlg, msg, deferred) {
    dlg.dialog({
        autoOpen : true,
        title : msg.title,
        modal : true,
        width : parseLen(msg.width, $(window).width()),
        height : parseLen(msg.height, $(window).height()),
        position : {
            my : "center",
            at : "center",
            of : window,
            collision : "none"
        },
        buttons : [ {
            text : "Close",
            click : function() {
                $(this).dialog('close');
            }
        } ],
        create : function(event, ui) {
            $(event.target).parent().css('position', 'fixed');
        },
        close : function(event, ui) {
            $(event.target).parents(".ui-dialog").detach();
            deferred.resolve();
        }
    });
}
