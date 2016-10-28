$(document).ready(function() {

    /**
     * Must be called in the iframe.
     */
    if (parent != window) {
        parent.postMessage('document-ready', '*');
    }

    if (String.prototype.endsWith == undefined) {
        String.prototype.endsWith = function(tail) {
            if (tail.length > this.length)
                return false;
            return this.substring(this.length - tail.length) == tail;
        };
    }

    function parseLen(v, ref, defval) {
        if (v == null)
            v = defval;

        if (typeof (v) == 'number')
            return v;

        v = String(v);
        if (v.endsWith("%")) {
            var ratio = v.substring(0, v.length - 1) / 100.0;
            v = ratio * ref;
        }
        return v;
    }

    function opendialog_handler(event) {
        var srcbtn = $(event.target).closest('.btn-opendialog');
        var qtmplab = $(srcbtn).closest('.qtmp').find('header .q').text();
        var page = srcbtn.attr("p");
        var id = srcbtn.attr("dialog-id");
        var url = srcbtn.attr("dialog-url");
        var title = srcbtn.attr("dialog-title");
        if (title == null)
            title = srcbtn.text();

        var dialog;
        var iframe;
        if (id != null)
            dialog = document.getElementById(id);
        if (dialog == null) {
            dialog = document.createElement('div');
            if (id != null)
                dialog.id = id;
            dialog.className = 'ZwkDialog' + (page == null ? '' : (' p' + page));

            iframe = document.createElement('iframe');
            iframe.setAttribute('src', url);
            dialog.appendChild(iframe);

            // Decorate the dialog using handler....
            document.body.appendChild(dialog);
        } else {
            iframe = $(dialog).find('iframe')[0];
        }

        var width = parseLen(srcbtn.attr("dialog-width"), $(window).width(), '75%');
        var height = parseLen(srcbtn.attr("dialog-height"), $(window).height(), '75%');

        $(dialog).dialog({
            autoOpen : true,
            title : title,
            modal : true,
            width : width,
            height : height,
            position : {
                my : "center",
                at : "center",
                of : window,
                collision : "none"
            },
            create : function(event, ui) {
                $(event.target).parent().css('position', 'fixed');
            },
            close : function(event, ui) {
                if (id == null)
                    $(event.target).parents(".ui-dialog").detach();
            }
        });

        var options = {};
        srcbtn.trigger('open', [ options ]);

        var controller = {
            opt : options,
            close : function(selection) {
                $(dialog).dialog('close');
                srcbtn.trigger('close', [ selection ]);
            }
        };

        // this works for non-first dialog open.
        iframe.contentWindow.dialog = controller;
        { // this is not enough.
            // the contentWindow can be invalid after iframe loaded.
            function messageHandler(e) {
                if (iframe.contentWindow == null) {
                    // the dialog is already closed (at early time).
                    window.removeEventListener('message', messageHandler);
                    return;
                }

                switch (e.data) {
                case "document-ready":
                    // XXX which iframe's document? not determined here.
                    // XXX and iframe loading-state is also unknown.
                    if (iframe.contentWindow != null)
                        iframe.contentWindow.dialog = controller;

                    // Don't remove me for 'close-dialog'.
                    break;

                case "close-dialog":
                    // "close-dialog: <selection>"
                    // e.data =~ s/^close-dialog://;
                    break;
                }
                window.removeEventListener('message', messageHandler);
            } // function messageHandler
            window.addEventListener('message', messageHandler, false);
        }
    }

    $.fn.openDialogButton = function() {
        $(this).click(function(event) {
            return opendialog_handler(event);
        });
    };

});
