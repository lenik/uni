import $ from 'jquery';

import 'datatables.net-dt/css/jquery.dataTables.css';

// import 'datatables.net-responsive';

import DataTables from 'datatables.net';

import type { Api, Config } from "datatables.net";

// import lang_de from 'datatables.net-plugins/i18n/de-DE.mjs';
import lang_zh from 'datatables.net-plugins/i18n/zh.mjs';

import type { SymbolCompileFunc, SetupDataFunc, ColumnType, OnAppliedFunc } from "./types";
import { getColumns, getColumnsConfig, getExtrasConfig, keepSelection, makePageSizeAuto } from "./utils";

import { configAjaxData } from './ajax-lily';
import { convertToDataRows } from './objconv';
import { showError } from 'skeljs-core/src/logging/api';

export function _useDataTable(table: any,
    configOverride: Config,
    compile: SymbolCompileFunc,
    setupData: SetupDataFunc,
    onApplied?: OnAppliedFunc
) {

    if (table == null) {
        showError("table is null.");
        return;
    }

    let $table = $(table);
    let dom = $table.attr("dom");

    let baseConfig: any = {
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
        baseConfig.paginate = true;
        baseConfig.paginationType = "full_numbers";
        // config.paginationType = "bootstrap";
        baseConfig.pageLength = 25;
        // config.pageResize = true;

        // config.lengthMenu = [ [ 10, 20, 50, 100, ], [ 10, 20, 50, 100 ] ];

        baseConfig.deferRender = true;
    }

    let columns = getColumns(table, compile);

    let config = $.extend({},
        baseConfig,
        getColumnsConfig(columns),
        getExtrasConfig(table),
        configOverride);

    if ($table.attr("config") != null) {
        let script = $table.attr("config");
        let f = eval("(function(config) {" + script + "})");
        f(config);
    }

    let setup = setupData(config, columns);

    // let dt = $table.DataTable(config);
    let dt = new DataTables($table, config);

    keepSelection(dt);
    makePageSizeAuto(dt);

    if (onApplied != null)
        onApplied(dt, table, setup);

    return dt;
}

export function useDataTable(table: HTMLElement, configOverride: Config, compile: SymbolCompileFunc, fetch: () => any) {
    let setupData = (config: Config, columns: ColumnType[]) => {
        let fields = columns.map(c => c.field);

        let loadData = () => {
            let dataTab = fetch();
            let rows = convertToDataRows(fields, dataTab.columns, dataTab.data);
            return rows;
        };

        let data = loadData();
        config.data = data;
        return loadData;
    };

    let onApplied = (dt: Api<any>, table: HTMLTableElement, setup: any) => {
        let loadData = setup;
        $(table!).data('reload', loadData);
    };

    return _useDataTable(table, configOverride, compile, setupData, onApplied);
}

export function useAjaxDataTable(table: HTMLElement, configOverride: Config, compile: SymbolCompileFunc) {
    let $table = $(table);
    let dataUrl = $table.data("url");
    let params = $table.data("params");

    let setupData = (config: Config, columns: ColumnType[]) => {
        configAjaxData(config, dataUrl, columns, params);
    };
    return _useDataTable(table, configOverride, compile, setupData);
}

// Remember the last selection after reload.
function reloadEnh(dt: Api<any>, onReloaded?: any, resetPaging?: boolean) {
    let selectedTr = dt.row(".selected").node();
    let lastId = $(selectedTr).data("id");
    return dt.ajax.reload(function () {
        if (lastId != null) {
            let tr = dt.row("[data-id=" + lastId + "]").node();
            $(tr).addClass("selected");
        }
        if (onReloaded != undefined)
            onReloaded();
    }, resetPaging);
}

DataTables.Api.register("ajax.reload_enh()", function (this: Api<any>) {
    reloadEnh(this, ...arguments);
});
