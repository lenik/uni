import $ from 'jquery';

function percent2(n: number): string {
    return Math.round(n * 10000) / 100 + "%";
}

$(() => {
    $(".resizable").append("<i class='geom'></i>");
    
    $(".resizable").draggable({
        drag: function() {
            update(this);
        }
    }).resizable({
        resize: function(e: Event) {
            update(this);
        }
    });

    function update(el: any) {
        var sel = $(el);
        var width = sel.width() || 0;
        var height = sel.height() || 0;

        var parent = sel.parent();
        var parentWidth = parent.width() || 1;
        var parentHeight = parent.height() || 1;

        var pos = sel.position();
        var s= pos.left + "," + pos.top + " size:" + sel.width() + "*" + sel.height();
        var r = "left: " + percent2(pos.left / parentWidth) + ";\n"
            + "top: " + percent2(pos.top / parentHeight) + ";\n"
            + "width: " + percent2(width / parentWidth) + ";\n"
            + "height: " + percent2(height / parentHeight) + ";\n";
        navigator.clipboard.writeText(r);
        $(".geom", el).html(r.replace(/;\n/g, "<br>"));
    }

});
