import $ from 'jquery';
// (window as any).jQuery = $;

import DataTables from 'datatables.net';
import type { Api } from 'datatables.net';

import { replaceLiteralOrRegex } from '@skeljs/core/src/lang/string.js';
import type { ColumnType, CreateOptions, SymbolCompileFunc } from './types';
import { compileOnCreate, compileRender, parseOrder, parseSpecParams } from './types';
import format from './formats';
import ITypeInfo from '@skeljs/core/src/lang/ITypeInfo';

// if ($.fn.DataTable == null) {
//     let dtn = DataTables;
//     console.error('$.fn.DataTable is null. autofixed.');
//     let fn = $.fn as any;
//     fn.dataTable = DataTables;
//     fn.dataTableSettings = (DataTables as any).settings;
//     fn.dataTableExt = DataTables.ext;
//     (DataTables as any).$ = $;
//     fn.DataTable = function (opts) {
//         return $(this).dataTable(opts).api();
//     };
//     fn.DataTable.Api = DataTables.Api;
// }

export function getColumns(table: any, options: CreateOptions): ColumnType[] {
    let $table = $(table);
    let columns: ColumnType[] = [];

    $("thead th", $table).each(function (position, th) {
        let $th = $(th);
        let column: ColumnType = {
            position: position,
            field: $th.data("field"),
            typeKey: $th.data('type'),
            priority: $th.data("priority") || 0,
            ascending: parseOrder($th.data("order")),
            styleClass: $th.attr('class'),
            oncreate: compileOnCreate($th.attr("oncreate")),
        };

        let typeInfo: ITypeInfo<any> | undefined = undefined;
        if (options.typeMap != null) {
            let defaultTypeKey = options.typeMap.STRING != null ? 'STRING' : undefined;
            column.typeKey ||= defaultTypeKey;
            if (column.typeKey != null)
                typeInfo = column.type = options.typeMap[column.typeKey];
        }

        let params = parseSpecParams($th.data("param")) || {};
        for (let i = 0; i < th.attributes.length; i++) {
            let attr = th.attributes[i];
            if (attr.name.startsWith("param:"))
                params[attr.name.substring(6)] = attr.value;
        }
        column.params = params;

        let dataFormat = $th.data("format");
        if (dataFormat != null) {
            let fn = options.compile(dataFormat);
            column.render = (data: any, type: string, row: any[], meta: any) => {
                return fn(data);
            };
        }

        else if (typeInfo != null) {
            column.render = (data: any, type: string, row: any[], meta: any) => {
                switch (type) {
                    case 'type':
                    case 'sort':
                    case undefined:
                        return data;
                }

                if (data == null)
                    return typeInfo!.nullText;
                let val: any = data;
                if (typeof data == 'string')
                    val = typeInfo!.parse(data);

                switch (type) {
                    case 'filter':
                        if (val == null)
                            return typeInfo!.nullText;
                        else
                            return typeInfo!.format(val);

                    case 'display':
                        let parent = document.createElement('div');
                        let content = typeInfo!.renderHtml(val);
                        if (content == undefined)
                            if (val == null)
                                return typeInfo!.nullText;
                            else
                                return typeInfo!.format(val);
                        else {
                            return content;
                        }
                    default:
                        throw new Error("unexpected render type: " + type);
                }
            };
        }

        // <script.render> overrides all.
        let renderScript = $("script.render", th).text() || $th.data("render");
        if (renderScript != null) {
            column.render = compileRender(renderScript);
        }

        columns.push(column);
    });
    return columns;
}

export function getColumnsConfig(columns: ColumnType[]) {

    function renderRegExp(data: any, type: string, row: any[], meta: any) {
        if (data == null)
            return;
        // slow:
        // let api = new $.fn.dataTable.Api(meta.settings);
        let th = $(meta.settings.aoColumns[meta.col].nTh);
        let pattern = th.attr("pattern");
        if (pattern == null) return;
        let replacement = th.attr("replacement") || "";
        data = replaceLiteralOrRegex(data, pattern, replacement);
    }

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

                if (c.typeKey != null)
                    $(cell).attr('data-type', c.typeKey!);

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

function _selectSingle(callback?: any) {
    this.on('click', 'tbody tr', (event: any) => {
        let tr = event.currentTarget as HTMLTableRowElement;
        (this.rows() as any).deselect();
        (this.row(tr) as any).select();
        return;
    });
}

$.fn.DataTable.Api.register("selectSingle()", function (this: Api<any>) {
    return _selectSingle.call(this, ...arguments);
});

function _autoPageSize(minPageSize: number = 1) {
    let table: HTMLTableElement = this.table().node() as HTMLTableElement;

    // if (paginated && $table.hasClass("page-resize")) {
    const wrapper = table.parentElement;
    if (wrapper == null)
        return;

    let outer: HTMLElement = wrapper.parentElement!;
    if (outer == null)
        return;
    if (outer.parentElement?.classList.contains('content'))
        outer = outer.parentElement;

    let setPageLength = () => {
        let outerHeight = $(outer).height() || 0;
        let wrapperHeight = $(wrapper!).height() || 0;

        // let headerHeight = $('thead', table).height() || 0;
        // let footerHeight = $('tfoot', table).height() || 0;
        let bodyHeight = $('tbody', table).height() || 0;

        // height(thead + tfoot + search + pagination)
        let dtAdds = wrapperHeight - bodyHeight;

        let wmTop = $(wrapper).css("margin-top");
        let wmBottom = $(wrapper).css("margin-bottom");
        let other = parseInt(wmTop.replace('px', '')) + parseInt(wmBottom.replace('px', ''));

        if (outer == wrapper.parentElement)
            for (let i = 0; i < outer.children.length; i++) {
                let child = outer.children[i];
                if (child.checkVisibility())
                    if (child != wrapper)
                        other += $(child).height() || 0;
            }

        let availableHeight = outerHeight - dtAdds - other;
        let _rowHeight;

        let rowNode: HTMLElement = this.row(0).node() as HTMLElement;
        if (rowNode == null) {
            let tbody = $('tbody', table)[0];
            let tr: HTMLTableRowElement = tbody.children[0] as HTMLTableRowElement;
            if (tr == null) {
                tr = document.createElement('tr');
                tbody.appendChild(tr);
                let td = document.createElement('td');
                td.textContent = '&nbsp;'
                tr.appendChild(td);
            }
            _rowHeight = tr.offsetHeight;
            tbody.removeChild(tr);
        } else {
            _rowHeight = rowNode.offsetHeight;
        }

        let rowHeight = _rowHeight || 20;
        rowHeight += 1; // border

        let rowsPerPage = Math.floor(availableHeight / rowHeight);
        if (rowsPerPage < minPageSize)
            rowsPerPage = minPageSize;

        debugHtml(outer, {
            outerHeight, wrapperHeight, bodyHeight, dtAdds, other,
            availableHeight, _rowHeight, rowHeight, rowsPerPage
        });

        let len = this.page.len();
        if (len != rowsPerPage) {
            this.page.len(rowsPerPage).draw();
        }
    };
    setPageLength();

    let resizable = $(table).closest(".resizable");
    if (resizable.length) {
        let observer = new ResizeObserver((mutations) => setPageLength())
        observer.observe(resizable[0]);
    } else {
        $(window).on('resize', setPageLength);
    }
}

$.fn.DataTable.Api.register("autoPageSize()", function (this: Api<any>) {
    return _autoPageSize.call(this, ...arguments);
});

function debugHtml(outer, vars: any) {
    for (let k in vars)
        vars[k] = format('decimal2', vars[k]);
    let outerName = "." + outer.className;
    let msg = `<span class='outer' title='${outerName}'>out ${vars.outerHeight}</span>`
        + `, <span class='wrapper'>wrapper ${vars.wrapperHeight}</span>`
        + `, <span class="tbody">tbody ${vars.bodyHeight}</span>`
        + `, <span class="adds" title="wrapper - tbody">adds ${vars.dtAdds}</span>`
        + `, <span class="other" title="outer - wrapper">other ${vars.other}</span>`
        + `, <span class="avail" title="outer - adds - other">avail ${vars.availableHeight}</span>`
        + `, row ${vars._rowHeight} => ${vars.rowHeight}`
        + `, num ${vars.rowsPerPage}`
        ;
    $('.debug', outer).html(msg);

    outer = $(outer);
    $('.debug .outer', outer).on('mouseenter', () => outer.addClass('highlight'));
    $('.debug .outer', outer).on('mouseleave', () => outer.removeClass('highlight'));

    let wrapper = $('.dataTables_wrapper', outer);
    $('.debug .wrapper').on('mouseenter', () => wrapper.addClass('highlight'));
    $('.debug .wrapper').on('mouseleave', () => wrapper.removeClass('highlight'));

    let tbody = $('tbody', wrapper);
    $('.debug .tbody').on('mouseenter', () => tbody.addClass('highlight'));
    $('.debug .tbody').on('mouseleave', () => tbody.removeClass('highlight'));
}