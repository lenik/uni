
function ajaxResultHandler(data, state, xhr) {
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
}

(function($) {

    window.ajaxResultHandler = ajaxResultHandler;

})(jQuery);
