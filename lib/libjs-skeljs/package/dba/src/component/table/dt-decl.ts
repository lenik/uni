import $ from 'jquery';

import DataTable from "datatables.net";
//import 'datatables.net-responsive';
import lang_de from 'datatables.net-plugins/i18n/de-DE.mjs';
import lang_zh from 'datatables.net-plugins/i18n/zh.mjs';

import { showError } from 'skeljs-core/src/logging/api.js';

import { objv2Tab } from './objconv.js';

// Remember the last selection after reload.
DataTable.Api.register("ajax.reload_enh()",
    function (cb: any, reset: boolean) {
        let model = this;
        let tr = this.row(".selected").node();
        let id = $(tr).data("id");
        return this.ajax.reload(function () {
            if (id != null) {
                let tr = model.row("[data-id=" + id + "]").node();
                $(tr).addClass("selected");
            }
            cb();
        }, reset);
    });

interface OrderInfo {
    priority?: number // column priority
    position: number // column position
    descending?: boolean
}

export default function makeDataTable(table: any, context: any) {
    let $table = $(table);
    let id = table.id;
    let dom = $table.attr("dom");

    let config: any = {
        // displayStart : 10,
        autoWidth: false,
        dom: dom != null ? dom : 'C<"clear">lfrtip',

        // by default ID-descend
        order: [[0, "desc"]],

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

    let dataUrl = $table.data("url");
    let dataParams = $table.data("params");
    let dataParamFormats = {};

    let fields: string[] = [];
    let fieldMappers = [];
    let fieldRenders = [];
    let cellCreations = [];

    let columnDefs: any[] = [
        { targets: "hidden", visible: false },
        { targets: "detail", visible: false },
        { targets: "no-search", searchable: false },
        { targets: "no-sort", orderable: false },
        { targets: "regexp", render: renderRegExp },
        {
            targets: "with-image", className: "with-image",
            render: function (a, b, c, d) { return renderWithImage(a, b, c, d, dataUrl); }
        },
    ];

    let orders: OrderInfo[] = [];

    $("thead th", $table).each(function (columnIndex, th) {
        let $th = $(th);
        let field = $th.data("field");
        let paramFormat = $th.data("field-param-format") || $th.data("param-format");
        let mapper = $th.data("field-mapper") || $th.data("mapper");
        let format = $th.data("field-format") || $th.data("format");
        let render = $th.data("field-render") || $th.data("render");
        let cellCreation = $th.attr("oncreatedcell");

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

        let dataClass = $th.data('class');
        if (dataClass != null) {
            columnDefs.push({
                targets: columnIndex, // [columnIndex],
                className: dataClass
            });
        }

        let dataType = $th.data('type');
        if (dataType != null) {
            columnDefs.push({
                targets: columnIndex, // [columnIndex],
                createdCell: function (td, cellData, rowData, row, col) {
                    $(td).attr('data-type', dataType);
                }
            });
        }

        let dataOrder = $th.data("order");
        if (dataOrder != null) {
            const priority = $th.data("priority") || 0;
            const descend = dataOrder == 'd'
                || dataOrder == 'desc'
                || dataOrder == 'descend';
            orders.push({
                priority: priority,
                position: columnIndex,
                descending: descend
            });
        }
    });

    $("th script.render", $table).each(function (index, script) {
        let $script = $(script);
        let js = $script.text();
        let $th = $($script.closest("th"));
        let fn = eval("(function (data, type, row) {" + js.trim() + "})");
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
        orders.sort((a, b) => {
            let p1 = a.priority || 0;
            let p2 = b.priority || 0;
            let cmp = p1 - p2;
            if (cmp != 0) return cmp;

            cmp = a.position - b.position;
            return cmp;
        });
        config.order = orders.map((a) => [a.position, a.descending ? 'desc' : 'asc']);
    }

    let dataVar = $table.data("let");
    if (dataVar != null) {
        function loadData() {
            let dataObj = context.getVar(dataVar);

            if (dataObj.columns == null) {
                dataObj = objv2Tab(dataObj);
            }

            // config.columns = dataObj.columns;
            let cmap = {};
            for (let i = 0; i < dataObj.columns.length; i++) {
                const h = dataObj.columns[i].title || dataObj.columns[i];
                cmap[h] = i;
            }

            const srcRows = dataObj.data;
            let projRows = srcRows.map(() => []);
            for (let fi = 0; fi < fields.length; fi++) {
                const fp = fields[fi];
                const dot = fp.indexOf('.');
                const head = dot == -1 ? fp : fp.substr(0, dot);
                let tailv = dot == -1 ? [] : fp.substr(dot + 1).split(/\./);
                const headCol = cmap[head];

                for (let y = 0; y < srcRows.length; y++) {
                    const row1 = srcRows[y];
                    const row2 = projRows[y];
                    let obj = null;
                    if (headCol != null) {
                        obj = row1[headCol];
                        for (let j = 0; j < tailv.length; j++) {
                            obj = obj[tailv[j]];
                            if (obj == null) break;
                        }
                    }
                    row2[fi] = obj;
                }
            }
            return projRows;
        }
        let data = loadData();
        config.data = data;
        $table.data('loader', loadData);
    }

    if (dataUrl != null) {
        let query = {
            row: "array", // or "object"
            columns: fields.join(","),
            formats: JSON.stringify(dataParamFormats)
        };
        if (dataParams != null) {
            try {
                let filter = eval('ans = ' + dataParams);
                $.extend(query, filter);
            } catch (err) {
                alert("Bad filter: " + err + "\n" + dataParams);
                return;
            }
        }

        let entityClass: string;
        let lastSlash = dataUrl.lastIndexOf('/');
        entityClass = dataUrl.substring(lastSlash + 1);

        config.ajax = function (data: any, callback, opts: any) {
            $.ajax({
                url: dataUrl + "/__data__",
                data: query,
                method: "POST"
            }).done(function (data: any) {
                if (typeof (data) != "object") {
                    showError("Invalid response: " + data);
                    return;
                }
                // preprocess rows
                data.rows.forEach(row => {
                    row._class = entityClass;
                });
                callback({ data: data.rows });
            }).fail(function (xhr: any, status: any, err) {
                showError("Failed to query index data: " + err);
            });
        };
    }

    if ($table.attr("no-colvis") != null)
        config.colVis = {
            buttonText: "列",
            label: function (index, title, th) {
                return (index + 1) + '. ' + title;
            },
            showAll: '显示全部'
        };

    if ($table.attr("no-tableTools") != null)
        config.tableTools = {
            sSwfPath: "../libjs/datatables/extensions/TableTools/swf/copy_csv_xls_pdf.swf"
        };

    if ($table.attr("config") != null) {
        let script = $table.attr("config");
        let cb = eval("(function(config) {" + script + "})");
        cb(config);
    }

    let dt = new DataTable(table, config);
    $table.data('dt', dt);

    dt.rows().on('click', 'tr', function (e) {
        let tr = $(this);
        let row = dt.row(this); // could be the header.
        let data = row.data();
        if (data == null)
            return null;

        let id = data[0];
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
        let wrapper = table.parentElement;
        let outer = wrapper.parentElement;

        function setPageLength() {
            let outerHeight = $(outer).height();
            let wrapperHeight = $(wrapper).height();

            // let headerHeight = $('thead', table).height() || 0;
            // let footerHeight = $('tfoot', table).height() || 0;
            let bodyHeight = $('tbody', table).height() || 0;
            let nonBody = wrapperHeight - bodyHeight;

            // let other = outerHeight - wrapperHeight;

            let availableHeight = outerHeight - nonBody; // - other;
            let rowNode = dt.row().node();
            if (rowNode == null) return;

            let rowHeight = rowNode.offsetHeight;
            if (rowHeight == 0) rowHeight = 1;

            let rowsPerPage = Math.floor(availableHeight / rowHeight);
            if (rowsPerPage < 10)
                rowsPerPage = 10;

            dt.page.len(rowsPerPage).draw();
        }
        setPageLength();
        $(window).on('resize', setPageLength);
    }

    return dt;
}

function renderRegExp(data: string | null, type: string, row: any[], meta: any) {
    if (data == null)
        return;
    // low performance:
    // let api = new $.fn.dataTable.Api(meta.settings);
    let th = $(meta.settings.aoColumns[meta.col].nTh);
    let pattern = th.attr("pattern");
    let replacement = th.attr("replacement");
    if (pattern == null)
        return;
    if (pattern.charAt(0) == "/") {
        pattern = pattern.substr(1);
        let lastSlash = pattern.lastIndexOf("/");
        let mode = pattern.substr(lastSlash + 1);
        pattern = pattern.substr(0, lastSlash);
        pattern = new RegExp(pattern, mode);
    }
    if (replacement == null) replacement = "";
    data = data.replace(pattern, replacement);
    return data;
}

function renderWithImage(data: string | null, type: string, row: any[], meta: any, dataHref) {
    if (type != "display")
        return data;
    // low performance:
    // let api = new $.fn.dataTable.Api(meta.settings);
    let th = $(meta.settings.aoColumns[meta.col].nTh);
    let script = $(th).attr("image");
    let fn = eval("(function (row) { return " + script + " })");
    let href = fn(row);
    if (href != null && href.length) {
        //href = dataHref + "/" + href;
        //href = alterHref(href);
        data = "<img src=\"" + href + "\"> <span>" + data + "</span>";
    }
    return data;
}
