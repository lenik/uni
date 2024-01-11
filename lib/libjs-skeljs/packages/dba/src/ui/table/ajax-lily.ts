import $ from 'jquery';

import type { Config } from "datatables.net";

import { baseName } from "@skeljs/core/src/io/url";
import { showError } from "@skeljs/core/src/logging/api";

import type { ColumnType } from "./types";
import { AjaxProtocol } from "./ajax";
import { convertToDataRows } from './objconv';

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

export function configAjaxData(config: Config, dataUrl: string, columns: ColumnType[], params?: any) {

    config.columnDefs?.push({
        targets: "with-image",
        className: "with-image",
        render: function (data: string | null, type: string, row: any[], meta: any) {
            return renderWithImage(data, type, row, meta, dataUrl);
        }
    });

    let fields = columns.map(c => c.field);
    let query = {
        row: "array", // or "object"
        columns: fields.join(","),
        formats: JSON.stringify(getFormats(columns)),
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

    config.ajax = function (data: any, callback, opts: any) {
        $.ajax({
            url: dataUrl,
            data: query,
            method: "POST"
        }).done(function (data: any) {
            if (typeof (data) != "object" || !Array.isArray(data.rows)) {
                showError("Invalid response: " + data);
                return;
            }

            let _rows: any[] = data.rows!;
            if (_rows.length == 0) {
                callback({ data: [] });
                return;
            }

            let head = _rows[0];
            let isArray = Array.isArray(head);

            // preprocess rows
            if (entityClass != null) {
                data.columns.push('_class');
                if (isArray)
                    _rows.forEach(row => row.push(entityClass));
                else
                    _rows.forEach(row => row._class = entityClass);
            }

            let rows = convertToDataRows(fields, data.columns, _rows);
            callback({ data: rows });
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