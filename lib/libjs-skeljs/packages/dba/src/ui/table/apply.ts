import $ from 'jquery';

import 'datatables.net-dt/css/jquery.dataTables.css';
// import 'datatables.net-bs/css/dataTables.bootstrap.css';
import 'datatables.net-select-dt/css/select.dataTables.css';

// import 'datatables.net-responsive';

import 'datatables.net';
import 'datatables.net-select';
// import 'datatables.net-bs5';
// import 'datatables.net-buttons-bs5';

import type { Api, Config } from "datatables.net";

// import lang_de from 'datatables.net-plugins/i18n/de-DE.mjs';
import lang_zh from 'datatables.net-plugins/i18n/zh.mjs';

import type { SymbolCompileFunc, SetupDataFunc, ColumnType, OnAppliedFunc } from "./types";
import { getColumns, getColumnsConfig, getExtrasConfig } from "./utils";

import { configAjaxData } from './ajax-lily';
import { convertToDataRows } from './objconv';
import { showError } from '@skeljs/core/src/logging/api';

export interface DataTableInstance {
    api: Api<any>,
    columns: ColumnType[]
}

export function _useDataTable(table: any,
    configOverride: Config,
    compile: SymbolCompileFunc,
    setupData: SetupDataFunc,
    onApplied?: OnAppliedFunc
): DataTableInstance {

    if (table == null)
        throw "table is null.";

    let $table = $(table);
    let dom = $table.attr("dom");

    let baseConfig: any = {
        // displayStart : 10,
        autoWidth: false,
        dom: dom != null ? dom : 'C<"clear">lfrtip',

        // responsive: true,
        language: lang_zh,
        deferred: true,
    };

    let state = $table.data('state');
    if (state != 'none') {
        switch (state) {
            case 'local':
                baseConfig.stateDuration = 0; break;
            case 'session':
                baseConfig.stateDuration = -1; break;
        }
        baseConfig.stateSave = true;
        baseConfig.stateSaveParams = function (settings, data) {
            for (var i = 0, ien = data.columns.length; i < ien; i++) {
                delete data.columns[i].visible;
            }
        };
    }

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

    if (columns.filter(c => c.ascending != null).length == 0) {
        // by default ID-descend
        baseConfig.order = [[0, "desc"]];
    }

    let idFields = columns.filter(c => c.field == 'id');
    if (idFields.length) {
        let simple = idFields.length == 1;
        let idColumnIndexes = idFields.map(f => f.position);
        baseConfig.createdRow = (row, data, dataIndex) => {
            let idVec = idColumnIndexes.map(pos => data[pos]);
            $(row).attr("pkey", idVec.join(","));
            $(row).data("id", simple ? idVec[0] : idVec);
        };
    }

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

    // console.log(config);
    let dataTableApi = $table.DataTable(config);
    // let dt = new DataTables($table, config);

    // dataTableApi.selectSingle();
    (dataTableApi as any).autoPageSize();

    if (onApplied != null)
        onApplied(dataTableApi, table, setup);

    return {
        api: dataTableApi,
        columns,
    };
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
    let fetchSizeAttr = $table.attr("fetch-size");
    let fetchSize = fetchSizeAttr == undefined ? undefined : parseInt(fetchSizeAttr);
    let params = $table.data("params");

    let processing = $table.attr('processing');
    let config = {
        language: {
            processing: processing
        }
    };
    config = $.extend(config, configOverride);

    let setupData = (config: Config, columns: ColumnType[]) => {
        configAjaxData(config, dataUrl, fetchSize, columns, params);
    };
    return _useDataTable(table, config, compile, setupData);
}

// Remember the last selection after reload.
function _reloadSmooth(resetPaging: boolean = false, onReloaded?: any) {
    let selectedTr = this.row(".selected").node();
    let lastId = $(selectedTr).data("id");

    this.iterator('table', function (settings) {
        settings.clearCache = true;
    });

    return this.ajax.reload(() => {
        if (lastId != null) {
            let tr = this.row("[data-id=" + lastId + "]").node();
            $(tr).addClass("selected");
        }
        if (onReloaded != undefined)
            onReloaded();
    }, resetPaging);
}

$.fn.DataTable.Api.register("ajax.reloadSmooth()", function (this: Api<any>) {
    return _reloadSmooth.call(this, ...arguments);
});

$.fn.DataTable.Api.register("clearCache()", function () {
    return this.iterator('table', function (settings) {
        settings.clearCache = true;
    });
});

function _rowNumInfo() {
    let currentNode = this.row({ selected: true }).node();
    let nodes = this.rows({ order: 'applied' }).nodes();
    let current: number | undefined = undefined;
    if (currentNode != null)
        current = nodes.indexOf(currentNode);
    return {
        nodes: nodes,
        n: nodes.length,
        pos: current,
    }
}

$.fn.DataTable.Api.register("rowNumInfo()", function (this: Api<any>) {
    return _rowNumInfo.call(this, ...arguments);
});