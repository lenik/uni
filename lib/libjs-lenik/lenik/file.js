// require: jquery
// require: jquery-fileupload

export function makeFileUpload(sel, configMore) {
    var config = {
        dataType: 'json',
        submit: function() {
            var progress = $(this).siblings(".progress");
            progress.show();
            beginWait();
        },
        always: function() {
            endWait();
            var progress = $(this).siblings(".progress");
            progress.hide();
        },
        progress: function(e, data) {
            var progress = $(this).siblings(".progress");
            var percent = Math.floor(data.loaded / data.total * 1000) / 10;
            var inner = $("div", progress);
            inner.text(percent + "%");
            inner.css("width", percent + "%");
        },
        done: function (e, data) {
            var $input = $(e.target).closest("[type=file]");
            var listref = $input.data("list");
            var lastDot = listref.lastIndexOf(".");
            var listctx = listref.substr(0, lastDot);
            var listkey = listref.substr(lastDot + 1);
            listctx = eval(listctx);
            if (listctx == null) {
                alert("Expected data-list-ctx.");
                return;
            }
            data.result.files.forEach(function (file, i) {
                var item = {
                    name: file.name,
                    size: file.size,
                    sha1: file.sha1,
                    href: file.url
                };
                if (listctx[listkey] == null) {
                    // app.life.properties.images = [];
                    window["app"].$set(listctx, listkey, []);
                }
                listctx[listkey].push(item);
            });
        }
    }

    config = $.extend({}, config, configMore);
    return sel.fileupload(config);
}

$(document).ready(function() {
    
    var extras = $("#file-extras");
    if (extras.length) {
        var html = extras.html();
        $("[type=file][data-list]").after(html);
    }
    
    makeFileUpload($("[type=file][data-list]"));
    
});
