// require: jquery
// require: jquery-ui

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
        function perc(n) {
            return Math.round(n * 10000) / 100;
        }
        function update(el) {
            var parent = $(el).parent();
            var pw = parent.width(), ph = parent.height();
            var pos = $(el).position();
            var s= pos.left + "," + pos.top + " size:" + $(el).width() + "*" + $(el).height();
            var r = "left: " + perc(pos.left / pw) + "%;\n"
                + "top: " + perc(pos.top / ph) + "%;\n"
                + "width: " + perc($(el).width() / pw) + "%;\n"
                + "height: " + perc($(el).height() / ph) + "%;\n";
            navigator.clipboard.writeText(r);
            $(".geom", el).html(r.replace(/;\n/g, "<br>"));
        }
    });
    
})(jQuery);
