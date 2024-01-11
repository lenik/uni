
import $ from 'jquery';
import dialog from "jquery-dialog";

import * as m from '../view/measure";

interface Message {
    title: string;
    width: number;
    height: number;
}

interface Deferred {
    resolve(): void;
}

export function showDialog(dlg: any, msg: Message, deferred: Deferred) {
    dlg.dialog({
        autoOpen : true,
        title : msg.title,
        modal : true,
        width : m.parseLen(msg.width, $(window).width()),
        height : m.parseLen(msg.height, $(window).height()),
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
        create : function(event: Event, ui: any) {
            $(event.target as any).parent().css('position', 'fixed');
        },
        close : function(event: Event, ui: any) {
            $(event.target as any).parents(".ui-dialog").detach();
            deferred.resolve();
        }
    });
}
