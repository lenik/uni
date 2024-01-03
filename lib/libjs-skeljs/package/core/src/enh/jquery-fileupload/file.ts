
import $ from 'jquery';
import * as ju from 'jquery-fileupload';
import * as waitbox from '../../skel/waitbox';

interface File {
    name: string
    size: number
    sha1: string
    url: string
}

var win: any = window;

export function makeFileUpload(sel: any, configMore: any = {}) {
    var config = {
        dataType: 'json',
        submit: function() {
            var progress = $(this).siblings(".progress");
            progress.show();
            waitbox.beginWait();
        },
        always: function() {
            waitbox.endWait();
            var progress = $(this).siblings(".progress");
            progress.hide();
        },
        progress: function(e: Event, data: any) {
            var progress = $(this).siblings(".progress");
            var percent = Math.floor(data.loaded / data.total * 1000) / 10;
            var inner = $("div", progress);
            inner.text(percent + "%");
            inner.css("width", percent + "%");
        },
        done: function (e: Event, data: any) {
            var $input = $(e.target as any).closest("[type=file]");
            var listref = $input.data("list");
            var lastDot = listref.lastIndexOf(".");
            var listctx = listref.substr(0, lastDot);
            var listkey = listref.substr(lastDot + 1);
            listctx = eval(listctx);
            if (listctx == null) {
                alert("Expected data-list-ctx.");
                return;
            }
            data.result.files.forEach(function (file: File, i: number) {
                var item = {
                    name: file.name,
                    size: file.size,
                    sha1: file.sha1,
                    href: file.url
                };
                if (listctx[listkey] == null) {
                    // app.life.properties.images = [];
                    win["app"].$set(listctx, listkey, []);
                }
                listctx[listkey].push(item);
            });
        }
    }

    config = $.extend({}, config, configMore);
    return sel.fileupload(config);
}

$(() => {
    
    var extras = $("#file-extras");
    if (extras.length) {
        var html = extras.html();
        $("[type=file][data-list]").after(html);
    }
    
    makeFileUpload($("[type=file][data-list]"));
    
});
