import DataTable from "datatables.net";
//import 'datatables.net-responsive';
import lang_de from 'datatables.net-plugins/i18n/de-DE.mjs';
import lang_zh from 'datatables.net-plugins/i18n/zh.mjs';

// Remember the last selection after reload.
DataTable.Api.register("ajax.reload_enh()", function (cb, reset) {
    var model = this;
    var tr = this.row(".selected").node();
    var id = $(tr).data("id");
    return this.ajax.reload(function() {
            if (id != null) {
                var tr = model.row("[data-id=" + id + "]").node();
                $(tr).addClass("selected");
            }
            cb();
        }, reset);
});

function objv2Tab(objv) {
    var colstat = {};
    for (var i = 0; i < objv.length; i++) {
        var obj = objv[i];
        if (obj == null) continue;
        for (var k in obj) {
            colstat[k] ||= 0;
            colstat[k]++;
        }
    }
    var cv = Object.keys(colstat);
    var columns = cv.map( (c) => ({ title: c}) );
    var data = [];
    for (var i = 0; i < objv.length; i++) {
        var obj = objv[i];
        var row = [];
        for (var ci = 0; ci < cv.length; ci++) {
            var k = cv[ci];
            var val = obj[k];
            row.push(val);
        }
        data.push(row);
    }
    return {
        columns: columns, 
        data: data
    };
}

export default function makeDataTable(table, context) {
    var $table = $(table);
    
    var id = table.id;
    if (id == 'monthFeesTab')
        console.log(id);

    var dom = $table.attr("dom");
    
    var config = {
        // displayStart : 10,
        autoWidth: false,
        dom: dom != null ? dom : 'C<"clear">lfrtip',
        
        // by default ID-descend
        order: [ [ 0, "desc" ] ],
        
        // responsive: true,
        language: lang_zh,
    };

    const paginated = dom == null || dom.includes('p');
    if (paginated) {
        config.paginate = true;
        config.paginationType = "full_numbers";
        // config.paginationType = "bootstrap";
        config.pageLength = 25;
        // config.pageResize = true;

        // config.lengthMenu = [ [ 10, 20, 50, 100, ], [ 10, 20, 50, 100 ] ];
        
        config.deferRender = true;
    }

    var dataUrl = $table.data("url");
    var dataParams = $table.data("params");
    var dataParamFormats = {};
        
    var fields = [];
    var fieldMappers = [];
    var fieldRenders = [];
    var cellCreations = [];
    
    var columnDefs = [
        { targets : "hidden", visible : false },
        { targets : "detail", visible : false },
        { targets : "no-search", searchable : false },
        { targets : "no-sort", orderable : false },
        { targets: "regexp", render: renderRegExp },
        { targets: "with-image", className: "with-image", 
            render: function(a,b,c,d) { return renderWithImage(a,b,c,d, dataUrl); } },
    ];
    
    var orders = [];

    $("thead th", $table).each(function(columnIndex, th) {
        var $th = $(th);
        var field   = $th.data("field");
        var paramFormat = $th.data("field-param-format") || $th.data("param-format");
        var mapper  = $th.data("field-mapper") || $th.data("mapper");
        var format  = $th.data("field-format") || $th.data("format");
        var render  = $th.data("field-render") || $th.data("render");
        var cellCreation = $th.attr("oncreatedcell");

        if (paramFormat != null)
            dataParamFormats[field] = paramFormat;
        
        if (mapper != null) {
            mapper = eval(mapper);
        }
        
        if (render != null) {
            render = eval("(function (data, type, row) {" + render.trim() + "})");
        } else {
            if (format != null) {
                // mapper = eval("(data) -> " + format);
                mapper = context.getFormat(format); 
                render = function (data, type, row) {
                    return mapper(data);
                };
            }
        }

        if (cellCreation != null) {
            cellCreation = eval(cellCreation);
        }

        fields.push(field);
        if (paramFormat != null)
            dataParamFormats[field] = paramFormat;
        
        fieldMappers.push(mapper);
        fieldRenders.push(render);
        cellCreations.push(cellCreation);

        var dataClass = $th.data('class');
        if (dataClass != null) {
            columnDefs.push({
                targets: columnIndex, // [columnIndex],
                className: dataClass
            });
        }
        
        var dataType = $th.data('type');
        if (dataType != null) {
            columnDefs.push({
                targets: columnIndex, // [columnIndex],
                createdCell: function(td, cellData, rowData, row, col) {
                    $(td).attr('data-type', dataType);
                }
            });
        }
        
        var dataOrder = $th.data("order");
        if (dataOrder != null) {
            const priority = $th.data("priority") || 0;
            const descend = dataOrder == 'd' 
                || dataOrder == 'desc' 
                || dataOrder == 'descend';
            orders.push({
                priority: priority,
                index: columnIndex,
                order: descend ? 'desc' : 'asc'
            });
        }
    });

    $("th script.render", $table).each(function (index, script) {
        var $script = $(script);
        var js = $script.text();
        var $th = $($script.closest("th"));
        var fn = eval("(function (data, type, row) {" + js.trim() + "})");
        fieldRenders[index] = fn;
    });
    fieldRenders.forEach(function (val, i) {
        if (val != null)
            columnDefs.push({
                targets: i,
                render: val
            });
    });
    cellCreations.forEach(function (val, i) {
        if (val != null)
            columnDefs.push({
                targets: i,
                createdCell: val
            });
    });
    
    config.columnDefs = columnDefs;

    if (orders.length) {
        orders.sort( (a, b) => a.priority - b.priority );
        config.order = orders.map( (a) => [ a.index, a.order ]);
    }

    var dataVar = $table.data("var");
    if (dataVar != null) {
        function loadData() {
            var dataObj = context.getVar(dataVar);

            if (dataObj.columns == null) {
                dataObj = objv2Tab(dataObj);
            }

            // config.columns = dataObj.columns;
            var cmap = {};
            for (var i = 0; i < dataObj.columns.length; i++) {
                const h = dataObj.columns[i].title || dataObj.columns[i];
                cmap[h] = i;
            }

            const srcRows = dataObj.data;
            var projRows = srcRows.map(()=>[]);
            for (var fi = 0; fi < fields.length; fi++) {
                const fp = fields[fi];
                const dot = fp.indexOf('.');
                const head = dot == -1 ? fp : fp.substr(0, dot);
                var tailv = dot == -1 ? [] : fp.substr(dot + 1).split(/\./);
                const headCol = cmap[head];

                for (var y = 0; y < srcRows.length; y++) {
                    const row1 = srcRows[y];
                    const row2 = projRows[y];
                    var obj = null;
                    if (headCol != null) {
                        obj = row1[headCol];
                        for (var j = 0; j < tailv.length; j++) {
                            obj = obj[tailv[j]];
                            if (obj == null) break;
                        }
                    }
                    row2[fi] = obj;
                }
            }
            return projRows;
        }
        var data = loadData();
        config.data = data;
        $table.data('loader', loadData);
    }

    if (dataUrl != null) {
        var query = {
            row: "array", // or "object"
            columns: fields.join(","),
            formats: JSON.stringify(dataParamFormats)
        };
        if (dataParams != null) {
            try {
                var filter = eval('ans = ' + dataParams);
                $.extend(query, filter);
            } catch (err) {
                alert("Bad filter: " + err + "\n" + dataParams);
                return;
            }
        }

        var entityClass;
        var lastSlash = dataUrl.lastIndexOf('/');
        entityClass = dataUrl.substring(lastSlash + 1);

        config.ajax = function(data, callback, opts) {
            $.ajax({
                url: dataUrl + "/__data__",
                data: query,
                method: "POST"
            }).done(function(data) {
                if (typeof(data) != "object") {
                    showError("Invalid response: " + data);
                    return;
                }
                // preprocess rows
                data.rows.forEach(row => {
                    row._class = entityClass;
                });
                callback({ data: data.rows });
            }).fail(function (xhr, status, err) {
                showError("Failed to query index data: " + err);
            });
        };
    }
    
    if ($table.attr("no-colvis") != null)
        config.colVis = {
            buttonText : "列",
            label : function(index, title, th) {
                return (index + 1) + '. ' + title;
            },
            showAll : '显示全部'
        };

    if ($table.attr("no-tableTools") != null)
        config.tableTools = {
            sSwfPath : "../libjs/datatables/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        };

    if ($table.attr("config") != null) {
        var script = $table.attr("config");
        var cb = eval("(function(config) {" + script + "})");
        cb(config);
    }

    var dt = new DataTable(table, config);
    $table.data('dt', dt);

    dt.rows().on('click', 'tr', function(e) {
        var tr = $(this);
        var row = dt.row(this); // could be the header.
        var data = row.data();
        if (data == null)
            return null;

        var id = data[0];
        if (id == undefined)
            return;

        if ($(this).hasClass("selected"))
            $(this).removeClass("selected");
        else {
            dt.$("tr.selected").removeClass("selected");
            $(this).addClass("selected");
        }

        //$(itab).trigger("rowClick", [ row ]);
    }); // row tr.onclick

    if (paginated && $table.hasClass("page-resize")) {
        var wrapper = table.parentElement;
        var outer = wrapper.parentElement;
        
        function setPageLength() {
            var outerHeight = $(outer).height();
            var wrapperHeight = $(wrapper).height();
            
            // var headerHeight = $('thead', table).height() || 0;
            // var footerHeight = $('tfoot', table).height() || 0;
            var bodyHeight = $('tbody', table).height() || 0;
            var nonBody = wrapperHeight - bodyHeight;

            // var other = outerHeight - wrapperHeight;
            
            var availableHeight = outerHeight - nonBody; // - other;
            var rowNode = dt.row().node();
            if (rowNode == null) return;

            var rowHeight = rowNode.offsetHeight;
            if (rowHeight == 0) rowHeight = 1;

            var rowsPerPage = Math.floor(availableHeight / rowHeight);
            if (rowsPerPage < 10)
                rowsPerPage = 10;
            
            dt.page.len(rowsPerPage).draw();
        }
        setPageLength();
        $(window).on('resize', setPageLength);
    }
    
    return dt;
}

function renderRegExp(data, type, row, meta) {
    if (data == null)
        return;
    // low performance:
    // var api = new $.fn.dataTable.Api(meta.settings);
    var th = $(meta.settings.aoColumns[meta.col].nTh);
    var pattern = th.attr("pattern");
    var replacement = th.attr("replacement");
    if (pattern == null)
        return;
    if (pattern.charAt(0) == "/") {
        pattern = pattern.substr(1);
        var lastSlash = pattern.lastIndexOf("/");
        var mode = pattern.substr(lastSlash + 1);
        pattern = pattern.substr(0, lastSlash);
        pattern = new RegExp(pattern, mode);
    }
    if (replacement == null) replacement = "";
    data = data.replace(pattern, replacement);
    return data;
}

function renderWithImage(data, type, row, meta, dataHref) {
    if (type != "display")
        return data;
    // low performance:
    // var api = new $.fn.dataTable.Api(meta.settings);
    var th = $(meta.settings.aoColumns[meta.col].nTh);
    var script = $(th).attr("image");
    var fn = eval ("(function (row) { return " + script + " })");
    var href = fn(row);
    if (href != null && href.length) {
        //href = dataHref + "/" + href;
        //href = alterHref(href);
        data = "<img src=\"" + href + "\"> <span>" + data + "</span>";
    }
    return data;
}
