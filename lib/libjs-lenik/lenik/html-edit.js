// require: jquery
// require: jquery-ui

function percent2(n) {
    return Math.round(n * 10000) / 100 + "%";
}

(function($) {
    
    $(document).ready(function() {
        $(".resizable").append("<i class='geom'></i>");
        
        $(".resizable").draggable({
            drag: function() {
                update(this);
            }
        }).resizable({
            resize: function(e) {
                update(this);
            }
        });


        function update(el) {
            var parent = $(el).parent();
            var pw = parent.width(), ph = parent.height();
            var pos = $(el).position();
            var s= pos.left + "," + pos.top + " size:" + $(el).width() + "*" + $(el).height();
            var r = "left: " + percent2(pos.left / pw) + ";\n"
                + "top: " + percent2(pos.top / ph) + ";\n"
                + "width: " + percent2($(el).width() / pw) + ";\n"
                + "height: " + percent2($(el).height() / ph) + ";\n";
            navigator.clipboard.writeText(r);
            $(".geom", el).html(r.replace(/;\n/g, "<br>"));
        }
    });
    
})(jQuery);
