// require: scripting

$(document).ready(function() {

    $("[data-control=combo] [data-val]").click(function() {
        var item = $(this);
        var val = item.data("val");
        var control = item.parent("[data-control]");
        var path = control.data("var");
        var v = resolvVar(path);
        v.ctx[v.path] = val;
        if (path == "app.state") {
            app.init(val);
        }
    });

    $("[data-control=multi] [data-var]").click(function(e) {
        var toggle = $(this).data("toggle");
        var single = ! e.ctrlKey;
        if (single)
            $(this).siblings().each(function() {
                var path = $(this).data("var");
                writeVar(path, false);
            });
        var path = $(this).data("var");
        var v = resolvVar(path);
        v.ctx[v.path] = single ? true : !v.value;
    });

    $("[data-toggle]").click(function() {
        var item = $(this);
        var path = item.data("toggle");
        var v = resolvVar(path);
        v.ctx[v.path] = ! v.value;
    });

    $(".editable").inlineEdit();

});
