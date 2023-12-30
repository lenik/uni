$(document).ready(function() {

    $(document.body).keydown(function (e) {
        var el = $(e.target);
        if (el.hasClass("editable")) return;
        if (el.is("input, select")) return;
        
        switch (e.keyCode) {
        case 27: // ESC
        case 32: // SPC
        case 46: // DEL
            return;
        }
    });

    // $("table.dataTable").attr("data-filter", "app.dataFilter");
    
    $(".cmds *[href]").click(function (e) {
        var $a = $(this);
        var cmd = $a.attr("href");
        var $table = $a.closest(".list-cmds").find(".dataTable");
        var model = $table.DataTable();
        
        var row = model.row(".selected").data();
        
        switch (cmd) {
            case "select":
                if (row == null)
                    return false;
                var map = {};
                model.columns().header().each(function (th, i) {
                    var field = $(th).data("field");
                    map[field] = row[i];
                });
                
                window.select(map);
                window.hide();
                break;
                
            case "close":
                window.hide();
                break;
                
            default:
                return;
        }
        return false;
    });
    
    //fullScreen();
    
});
