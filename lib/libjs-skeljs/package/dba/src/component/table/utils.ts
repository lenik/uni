import $ from 'jquery';

import DataTables from 'datatables.net';
import type { Api } from 'datatables.net';

import type { ColumnType, SymbolCompileFunc } from './types';
import { compileOnCreate, compileRender, parseOrder, parseSpecParams } from './types';

import { objv2Tab } from './objconv.js';

export function getColumns(table: any, compile: SymbolCompileFunc) {
    let $table = $(table);
    let columns: ColumnType[] = [];

    $("thead th", $table).each(function (position, th) {
        let $th = $(th);
        let column: ColumnType = {
            position: position,
            field: $th.data("field"),
            type: $th.data('type'),
            priority: $th.data("priority") || 0,
            ascending: parseOrder($th.data("order")),
            styleClass: $th.data('class'),
            oncreate: compileOnCreate($th.attr("oncreate")),
        };

        let params = parseSpecParams($th.data("param")) || {};
        for (let i = 0; i < th.attributes.length; i++) {
            let attr = th.attributes[i];
            if (attr.name.startsWith("param:"))
                params[attr.name.substring(6)] = attr.value;
        }
        column.params = params;

        let format = $th.data("format");
        if (format != null) {
            let fn = compile(format);
            column.render = function (data: string | null, type: string, row: any[], meta: any) {
                return fn(data);
            };
        }

        let script = $("script.render", th).text();
        let render = script || $th.data("render");
        if (render != null) { // override
            column.render = compileRender(render);
        }

        columns.push(column);
    });
    return columns;
}

export function getColumnsConfig(columns: ColumnType[]) {
    let config: any = {};
    let columnDefs: any[] = [
        { targets: "hidden", visible: false },
        { targets: "detail", visible: false },
        { targets: "no-search", searchable: false },
        { targets: "no-sort", orderable: false },
        { targets: "regexp", render: renderRegExp },
    ];

    let dtColumns: any[] = [];

    columns.forEach(c => {
        let dtColumn: any = null;
        // let def = (cfg: any) => columnDefs.push({ targets: c.position, ...cfg });
        let def = (cfg: any) => {
            for (let k in cfg) {
                if (dtColumn == null) dtColumn = {};
                dtColumn[k] = cfg[k];
            }
        };

        def({
            title: c.label,

            createdCell: function (cell: HTMLElement,
                cellData: AnalyserNode, rowData: AnalyserNode,
                row: number, col: number) {

                $(cell).attr('data-field', c.field);

                if (c.type != null)
                    $(cell).attr('data-type', c.type!);

                if (c.oncreate != null)
                    c.oncreate(cell, cellData, rowData, row, col);
            }
        });

        if (c.styleClass != null)
            def({ className: c.styleClass });
        if (c.render != null)
            def({ render: c.render });

        dtColumns.push(dtColumn);
    });
    config.columnDefs = columnDefs;
    config.columns = dtColumns;

    let orders = columns.filter(c => c.ascending != null);
    if (orders.length) {
        orders.sort((a, b) => {
            let p1 = a.priority || 0;
            let p2 = b.priority || 0;
            let cmp = p1 - p2;
            if (cmp != 0) return cmp;

            cmp = a.position - b.position;
            return cmp;
        });
        config.order = orders.map((a) => [a.position, a.ascending ? 'asc' : 'desc']);
    }
    return config;
}

export function getExtrasConfig(table: any) {
    let $table = $(table);
    let config: any = {};
    if ($table.attr("no-colvis") != null)
        config.colVis = {
            buttonText: "列",
            label: function (index: number, title: string, th: HTMLElement) {
                return (index + 1) + '. ' + title;
            },
            showAll: '显示全部'
        };

    if ($table.attr("no-tableTools") != null)
        config.tableTools = {
            // legacy: 
            sSwfPath: "datatables-tabletools/swf/copy_csv_xls_pdf.swf"
        };
    return config;
}

export function keepSelection(dt: Api<any>) {

    dt.rows().on('click', 'tr', function (this: HTMLElement, event: any) {
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
}

export function makePageSizeAuto(dt: Api<any>) {
    let table: HTMLTableElement = dt.table().node() as HTMLTableElement;

    // if (paginated && $table.hasClass("page-resize")) {
    const wrapper = table.parentElement;
    if (wrapper == null)
        return;

    const outer = wrapper.parentElement;
    if (outer == null)
        return;

    function setPageLength() {
        let outerHeight = $(outer!).height() || 0;
        let wrapperHeight = $(wrapper!).height() || 0;

        // let headerHeight = $('thead', table).height() || 0;
        // let footerHeight = $('tfoot', table).height() || 0;
        let bodyHeight = $('tbody', table).height() || 0;
        let nonBody = wrapperHeight - bodyHeight;

        // let other = outerHeight - wrapperHeight;

        let availableHeight = outerHeight - nonBody; // - other;
        let rowNode: HTMLElement = dt.row(0).node() as HTMLElement;
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

function renderRegExp(data: string | null, type: string, row: any[], meta: any) {
    if (data == null)
        return;
    // slow:
    // let api = new $.fn.dataTable.Api(meta.settings);
    let th = $(meta.settings.aoColumns[meta.col].nTh);
    let pattern = th.attr("pattern");
    if (pattern == null) return;
    let replacement = th.attr("replacement") || "";
    data = _replace(data, pattern, replacement);
}

function _replace(s: string, pattern: string, replacement: string) {
    if (pattern.charAt(0) == "/") {
        pattern = pattern.substring(1);
        let lastSlash = pattern.lastIndexOf("/");
        let mode = pattern.substring(lastSlash + 1);
        pattern = pattern.substring(0, lastSlash);
        let regex = new RegExp(pattern, mode);
        return s.replace(regex, replacement);
    } else {
        return s.replace(pattern, replacement);
    }
}
