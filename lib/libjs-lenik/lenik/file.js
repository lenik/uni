// require: jquery
// require: jquery-fileupload

$(document).ready(function() {

    $("[type=file][data-list]").fileupload({
        dataType: 'json',
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
                    window["app"].$set(listctx, "images", []);
                }
                listctx["images"].push(item);
            });
        }
    });
    
});
