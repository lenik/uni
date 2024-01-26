import $ from 'jquery';

import type { Config } from "datatables.net";

import { isEqual } from 'lodash-es';

import { baseName } from "@skeljs/core/src/io/url";
import { showError } from "@skeljs/core/src/logging/api";

import { AjaxProtocol } from "./ajax";
import { convertToDataRows } from './objconv';
import type { ColumnType } from "./types";

export interface TableData {
    columns: string[]
    rows: any[]
}

function getFormats(columns: ColumnType[]) {
    let paramFieldMap: any = {};
    columns.filter(c => c.params != null).forEach(c => {
        for (let paramName in c.params) {
            if (paramFieldMap[paramName] == undefined)
                paramFieldMap = {};
            paramFieldMap[c.field] = c.params[paramName];
        }
    });
    return paramFieldMap.format;
}

export class Lily extends AjaxProtocol {

    entityClass: string     // simple name
    addClassColumn: boolean

    constructor(controllerUrl: string) {
        super(controllerUrl + "/__data__" + baseName(controllerUrl));
        this.entityClass = baseName(controllerUrl);
        this.addClassColumn = true;
    }

    getParameters(columns: ColumnType[]) {
        let fields = columns.map(f => f.field).join(", ");
        let formats = columns.map(f => f.format);
        return {
            row: "array",
            columns: fields,
            formats: JSON.stringify(formats)
        }
    }

    toRowArray(_data: any): any[][] {
        let data: TableData = _data;
        if (data == null)
            throw "null response";
        if (!Array.isArray(data.rows))
            throw "no rows array in the response data";
        if (this.addClassColumn) {
            if (data.rows.length > 0) {
                let firstRow = data.rows[0];
                if (Array.isArray(firstRow)) {
                    data.columns.push('_class');
                    data.rows.forEach(row => { row.push(this.entityClass); });
                } else if (typeof 'firstRow' == 'object') {
                    data.rows.forEach(row => {
                        row._class = this.entityClass;
                    });
                }
            }
        }
        return data.rows;
    }
}

let defaultFetchSize = 500;

export function configAjaxData(config: Config, dataUrl: string, fetchSize: number | undefined,
    columns: ColumnType[], params?: any) {
    let batch = fetchSize || defaultFetchSize;
    let ratio = 0.2;
    let before = Math.floor(batch * ratio);

    config.columnDefs?.push({
        targets: "with-image",
        className: "with-image",
        render: function (data: string | null, type: string, row: any[], meta: any) {
            return renderWithImage(data, type, row, meta, dataUrl);
        }
    });

    let fields = columns.map(c => c.field);
    let query: any = {
        row: "array", // or "object"
        columns: fields.join(","),
        formats: JSON.stringify(getFormats(columns)),
        counting: true, // want total count
    };

    if (params != null) {
        if (typeof params == 'string') {
            try {
                params = eval('ans = ' + params);
            } catch (err) {
                throw "Bad filter: " + err + "\n" + params;
            }
        }
        $.extend(query, params);
    }

    let entityClass = getEntityClassFromUrl(dataUrl) || 'x';

    // row-num => row
    let cache: any[] = [];
    let rowCountCache = -1;
    let totalCountCache = -1;
    let cacheOfMode: any = {};

    config.serverSide = true;
    config.processing = true;
    let templ = config.language?.processing;
    if (templ != null) {
        let templElm = $(templ)[0];
        if (templElm != null) {
            templ = templElm.outerHTML;
            templElm.remove();
            config.language ||= {};
            config.language.processing = templ;
        }
    }

    // will be called when sort/search/page changes.
    config.ajax = async function (viewData: any, drawCallback, opts: any) {
        let mode = {
            search: viewData.search.value,
            order: viewData.order.map((a: any) =>
                (a.dir == 'asc' ? '' : '~') + columns[a.column].field)
        };

        let hit = true;
        if (!isEqual(cacheOfMode, mode)) {
            cache = [];
            hit = false;
        }

        let viewEnd = viewData.start + viewData.length;
        if (totalCountCache != -1 && viewEnd > totalCountCache)
            viewEnd = totalCountCache;

        for (let i = viewData.start; i < viewEnd; i++)
            if (cache[i] == undefined) {
                hit = false;
                break;
            }

        if (hit) {
            let slice = cache.slice(viewData.start, viewEnd);
            drawCallback({
                data: slice,
                draw: viewData.draw,
                recordsFiltered: totalCountCache,
                recordsTotal: totalCountCache,
            });
            return;
        }

        let start = viewData.start;
        start -= start % batch;
        start -= before;
        if (start < 0) start = 0;
        if (start + batch < viewEnd)
            batch = viewEnd - start;

        query['search-text'] = mode.search;
        query.order = mode.order.join(',');
        query['page.offset'] = start;
        query['page.limit'] = batch;
        query.seq = viewData.draw;

        console.log("cache miss: load -> offset " + start + " limit " + batch);
        console.log("    query data: " + JSON.stringify(query));

        await $.ajax({
            url: dataUrl,
            data: query,
            method: "POST"
        }).done(function (data: any) {
            if (typeof (data) != "object" || !Array.isArray(data.rows)) {
                showError("Invalid response: " + data);
                return;
            }

            let anyList: any[] = data.rows!;
            let rows: any[] = [];
            if (anyList.length) {
                let head = anyList[0];
                let isArray = Array.isArray(head);

                // preprocess rows
                if (entityClass != null) {
                    data.columns.push('_class');
                    if (isArray)
                        anyList.forEach(row => row.push(entityClass));
                    else
                        anyList.forEach(row => row._class = entityClass);
                }

                rows = convertToDataRows(fields, data.columns, anyList);
            }

            rowCountCache = data.rowCount;
            // assert data.rowCount == rows.length;
            // console.log('loaded row count: ' + data.rowCount);
            for (let i = 0; i < rows.length; i++)
                cache[start + i] = rows[i];
            cacheOfMode = mode;
            totalCountCache = data.totalCount;

            let slice = cache.slice(viewData.start, viewData.start + viewData.length);

            drawCallback({
                data: slice,
                draw: data.seq,
                recordsFiltered: totalCountCache,
                recordsTotal: totalCountCache,
            });
        }).fail(function (xhr: any, status: any, err) {
            showError("Failed to query index data: " + err);
        });
    };

    return config;
}

function renderWithImage(data: string | null, type: string, row: any[], meta: any, dataHref: string) {
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

function getEntityClassFromUrl(url: string): string | undefined {
    let ques = url.indexOf('?');
    if (ques != -1)
        url = url.substring(0, ques);

    let lastSlash = url.lastIndexOf('/');
    if (lastSlash == -1)
        return undefined;

    let base = url.substring(lastSlash + 1);
    if (!(base.startsWith("__data__")
        || base.startsWith("dataList")
        || base == ""))
        return undefined;

    let dir = url.substring(0, lastSlash);
    let entityClass = baseName(dir);
    return entityClass;
}