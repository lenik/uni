
var win: any = window;

$(() => {

    // $("table.dataTable").attr("data-filter", "app.dataFilter");
    
    $(".cmds *[href]").on('click', function (e) {
        var $a = $(this);
        var cmd = $a.attr("href");
        var $table = $a.closest(".list-cmds").find(".dataTable");
        var dt = $table.DataTable();
        
        var row = dt.row(".selected").data();
        
        switch (cmd) {
            case "select":
                if (row == null)
                    return false;
                var map = {};
                dt.columns().header().each(function (th, i) {
                    var field = $(th).data("field");
                    map[field] = row[i];
                });
                
                win.select(map);
                win.hide();
                break;
                
            case "close":
                win.hide();
                break;
                
            default:
                return;
        }
        return false;
    });
    
    //fullScreen();
    
});
