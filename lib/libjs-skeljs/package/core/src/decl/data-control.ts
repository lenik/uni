import $ from 'jquery';

import { resolveVar, writeVar } from '../lang/scripting";

$(() => {

    $("[data-control=combo] [data-val]").on('click', function() {
        var item = $(this);
        var val = item.data("val");
        var control = item.parent("[data-control]");
        var path = control.data("var");
        var v = resolveVar(path);
        v.ctx[v.path] = val;
        if (path == "app.state") {
            app.init(val);
        }
    });

    $("[data-control=multi] [data-var]").on('click', function(event) {
        var toggle = $(this).data("toggle");
        var single = ! event.ctrlKey;
        if (single)
            $(this).siblings().each(function() {
                var path = $(this).data("var");
                writeVar(path, false);
            });
        var path = $(this).data("var");
        var v = resolveVar(path);
        v.ctx[v.path] = single ? true : !v.value;
    });

    $("[data-toggle]").on('click', function() {
        var item = $(this);
        var varName = item.data("toggle");
        var v = resolveVar(varName);
        v.ctx[v.path] = ! v.value;
    });

    $(".editable").inlineEdit();

});
