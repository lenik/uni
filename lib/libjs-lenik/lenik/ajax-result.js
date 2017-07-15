(function($) {

    if (String.prototype.endsWith == undefined) {
        String.prototype.endsWith = function(tail) {
            if (tail.length > this.length)
                return false;
            return this.substring(this.length - tail.length) == tail;
        };
    }

    function parseLen(v, ref, defval) {
        if (v == null)
            return defval;

        if (typeof (v) == 'number')
            return v;

        v = String(v);
        if (v.endsWith("%")) {
            var ratio = v.substring(0, v.length - 1) / 100.0;
            v = ratio * ref;
        }
        return v;
    }

    function showDialog(dlg, msg, deferred) {
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

    window.ajaxResultHandler = function(data, state, xhr) {
        var context = this;
        var dv = [];

        if (data.messages != null) {
            for ( var i = 0; i < data.messages.length; i++) {
                var msg = data.messages[i];
                if (msg.html == null && msg.text != null)
                    msg.html = msg.text; // escape <>&...

                var div = $(document.createElement('div')).hide();
                div.addClass("ajax-message");
                div.html(msg.html);

                switch (msg.type) {
                case "msgbox":
                    $(document.body).append(div);
                    var deferred = $.Deferred();
                    dv.push(deferred);
                    showDialog(div, msg, deferred);
                    break;

                case "inline":
                    div.appendTo(context).fadeIn();
                    break;

                case "notice":
                case "log":
                    break;
                } // switch type
            } // for message
        } // if data.message

        if (data.updates != null) {
            for ( var k in data.updates) {
                var dst = document.getElementById(k);
                var html = data.updates[k];
                $(dst).html(html);
            }
        }

        var join = $.Deferred();
        $.when.apply($, dv).done(function() {
            join.resolve(data, status, xhr);
        });
        return join;
    };

})(jQuery);
