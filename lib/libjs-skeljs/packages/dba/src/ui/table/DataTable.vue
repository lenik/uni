<script lang="ts">

import $ from 'jquery';
import { ref, onMounted, computed, inject } from "vue";
import { Api } from "datatables.net";

import { typeMap as baseTypeMap } from '@skeljs/core/src/lang/baseinfo';
import { typeMap as timeTypeMap } from '@skeljs/core/src/lang/time';
import { typeMap as basTypeMap } from '@skeljs/core/src/lang/bas-info';

import { bool } from "@skeljs/core/src/ui/types";
import { baseName } from "@skeljs/core/src/io/url";
import { _throw, showError } from "@skeljs/core/src/logging/api";

import { ColumnType, DataTab, IDataTypeMap, Selection, SymbolCompileFunc } from "./types";
import { DataTableInstance, useAjaxDataTable, useDataTable } from "./apply";
import formats from "./formats";
import { objv2Tab } from "./objconv";

export interface Props {

    caption?: string
    captionPosition?: 'top' | 'bottom' | 'both'

    dataObjv?: any[]

    // columns?: ColumnType[]
    dataTab?: DataTab

    dataUrl?: string
    lilyUrl?: string // lily-specific: entity controller URL

    typeMap?: IDataTypeMap
    compile?: SymbolCompileFunc

    config?: any
    multi?: boolean | string

    /**
     * when version changed, a full-update will be made.
     */
    version?: number

    pageSize?: string | number
    hBand?: boolean
    vBand?: boolean

    watch?: boolean

    nullValue?: any
    debug?: boolean
}

const defaultTypeMap: IDataTypeMap = Object.assign({}, baseTypeMap, timeTypeMap, basTypeMap);

</script>

<script setup lang="ts">
defineOptions({
    inheritAttrs: false
})

const selection = defineModel<Selection>();

const props = withDefaults(defineProps<Props>(), {
    captionPosition: 'bottom',
    typeMap: defaultTypeMap,
    compile: formats,
    multi: false,
    version: 0,
    pageSize: 'auto',
    hBand: true,
    vBand: false,
    watch: false,
    nullValue: '\\N',
});

const emit = defineEmits<{
    select: [selection: Selection]
}>();

// computed properties

const captionAtTop = computed(() => props.caption != null
    && (props.captionPosition == 'top'
        || props.captionPosition == 'both'
    ));
const captionAtBottom = computed(() => props.caption != null
    && (props.captionPosition == 'bottom'
        || props.captionPosition == 'both'
    ));

const actualDataUrl = computed(() => {
    if (props.dataUrl != null)
        return props.dataUrl;
    if (props.lilyUrl != null) {
        let url = props.lilyUrl;
        if (url.endsWith("/"))
            url = url.substring(0, url.length - 1);
        let classHint = baseName(url);
        let dataUrl = url + "/__data__" + classHint;
        return dataUrl;
    }
    return undefined;
});

const debugDisplay = computed(() => bool(props.debug) ? 'block' : 'none');

// DataTable shortcuts

const dataTableCss = [
    "stripe",
    "hover",
    "compact",
    "responsive",
    "order-column",
    "row-border",
    "cell-border",
];

const tableRef = ref<HTMLTableElement>();
const api = ref<Api<any>>();
const columns = ref<ColumnType[]>();

const selectedRows = computed(() => {
    let dt = api.value;
    return dt?.rows({ selected: true }).data();
});
const selectedRowIndexes = computed(() => {
    let dt = api.value;
    return dt?.rows({ selected: true }).indexes();
});

defineExpose({
    api, columns,//
    deleteSelection, reconfigure, reset,
    loadDebugger,
});

function reset() {
    reconfigure(a => a);
}

function reconfigure(refactorFn: (settings: any) => any) {
    let dt = api.value;
    if (dt == null) return;

    let settings = dt.settings;
    let newSettings = refactorFn(settings);
    dt.destroy();
    initDataTable();
}

function initDataTable() {
    let tableElement: HTMLElement = tableRef.value!;

    let config = props.config || {};

    if (props.pageSize != null)
        if (props.pageSize == 'auto')
            config.autoPageSize = true;
        else {
            switch (typeof props.pageSize) {
                case 'string': config.pageLength = parseInt(props.pageSize); break;
                case 'number': config.pageLength = props.pageSize; break;
            }
        }

    let multiSelect = bool(props.multi);
    if (multiSelect)
        config.select = {
            info: true,
            style: 'multi+shift',
            selector: 'td',
        };

    let instance: DataTableInstance | undefined;

    let options = {
        compile: props.compile,
        typeMap: props.typeMap,
    };

    if (props.dataTab != null) {
        let fetch = () => props.dataTab;
        instance = useDataTable(tableElement, config, fetch, options);
    }

    else if (props.dataObjv != null) {
        let fetch = () => {
            let dataTab = objv2Tab(props.dataObjv!);
            return dataTab;
        };
        instance = useDataTable(tableElement, config, fetch, options);
    }

    else {
        let dataUrl = actualDataUrl.value;
        if (dataUrl != null) {
            $(tableElement).data('url', dataUrl);
            instance = useAjaxDataTable(tableElement, config, options);
        }
    }

    if (instance == null) {
        throw new Error('invalid use of <DataTable>');
    }

    api.value = instance.api;
    columns.value = instance.columns;

    if (!multiSelect)
        (instance.api as any).selectSingle(onDtSelect);

    instance.api.on('select', (e, dt, type, indexes) => onDtSelect(e, dt, type, indexes, true));
    // dt.on('deselect', (e, dt, type, indexes) => selectionChange(e, dt, type, indexes, true));

    // dt.on('click', 'tbody td:not(:first-child)', function (e) {
    //     editor.inline(this);
    // });
}

function onDtSelect(e: Event, dt: Api<any>, type: string, indexes: number[], select: boolean) {
    let selectedRows = dt.rows({ selected: true }).data().toArray() as any[][];
    let selectedIndexes = dt.rows({ selected: true }).indexes().toArray() as number[];
    let sel = new Selection(columns.value!, selectedRows, selectedIndexes);
    selection.value = sel;
    emit('select', sel);
}

function deleteSelection() {
    let dt = api.value;
    dt!.rows({ selected: true }).remove().draw(false);
}

function loadDebugger() {
    var url = 'https://debug.datatables.net/bookmarklet/DT_Debug.js';
    const DT_Debug = (window as any).DT_Debug;
    if (typeof DT_Debug != 'undefined') {
        if (DT_Debug.instance !== null) {
            DT_Debug.close();
        } else {
            new DT_Debug();
        }
    } else {
        var n = document.createElement('script');
        n.setAttribute('language', 'JavaScript');
        n.setAttribute('src', url + '?rand=' + new Date().getTime());
        document.body.appendChild(n);
    }
}

onMounted(() => {
    initDataTable();
    let dbg = inject('DT_Debug', false);
    if (dbg)
        loadDebugger();
});

</script>

<template>
    <div class="dt-container">
        <div class="caption" v-if="captionAtTop">{{ caption }}</div>
        <table ref="tableRef" class="dataTable second" :class="dataTableCss" v-bind="$attrs" :data-url="dataUrl"
            :lily-url="lilyUrl">
            <thead>
                <tr>
                    <slot>
                    </slot>
                </tr>
            </thead>
            <tbody>
                <slot name="body">
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                </slot>
            </tbody>
            <slot name="foot"></slot>
        </table>
        <div class="caption" v-if="captionAtBottom">{{ caption }}</div>
        <div class="debug"></div>
    </div>
</template>

<style lang="scss">
body {
    counter-reset: dataTable;
}

table.dataTable {
    counter-increment: dataTable;
}
</style>

<style lang="scss" scoped>
.caption {
    text-align: center;
    font-style: italic;

    &::before {
        content: 'Table ' counter(dataTable) ': ';
    }
}


::v-deep(.dataTables_wrapper) {
    // border: solid 2px yellow;
    position: relative;

    .dataTables_filter,
    .dataTables_info,
    .dataTables_paginate {
        display: block;
        position: relative;
        font-size: 85%;
        font-weight: 300;
        padding-top: 0;

        input {
            margin: 0 0 .2em .5em;
            border: none;
            border-bottom: dashed 1px hsl(200, 50%, 40%);
            border-radius: 0;
            padding: 0 .5em;
            font-weight: 300;
            font-family: monospace;

            &:focus-visible {
                outline: none;
            }
        }
    }


    .dataTables_filter {
        color: hsl(200, 50%, 40%);
    }

    .dataTables_info,
    .dataTables_paginate {
        line-height: 1em;
        top: 1em;
        transform: translateY(-50%);
        // margin-bottom: 1.5em;
    }

    .dataTables_paginate {

        .paginate_button {
            background: none;
        }

        .paginate_button.current {
            background: pink;
        }

        .paginate_button,
        .paginate_button.current {
            padding: .2em 0;
            border: none;
            border-radius: 3px;

            &:not(:last-child) {
                margin: 0 calc(-1* var(--bar-width));
            }

            --bar-width: 3px;
            --bar-padding: 5px;
            --bar-color: hsl(220, 30%, 60%);

            &::before {
                content: ' ';
                padding-right: var(--bar-padding);
                box-sizing: border-box;
                border-left-style: solid;
                border-left-width: var(--bar-width);
                border-left-color: transparent;
            }

            &::after {
                content: ' ';
                padding-left: var(--bar-padding);
                border-right-style: solid;
                border-right-width: var(--bar-width);
                border-right-color: transparent;
            }

            &:hover {
                color: hsl(220, 30%, 30%) !important;
                background: hsl(220, 30%, 94%);

                &::before,
                &::after {
                    content: ' ';
                    border-color: var(--bar-color);
                }
            }
        }
    }
}

// v-deep() since the content are dynamically changed.
table.dataTable::v-deep() {

    font-size: 85%;
    user-select: none;

    >thead>tr>th {
        background: hsl(200, 60%, 94%);
        color: hsl(200, 60%, 20%);
        font-weight: 500;
        // text-align: center;
        // font-family: serif;
    }

    >tbody>tr {

        &:hover,
        &:hover.selected {
            >* {
                background-color: #dee;
                box-shadow: none !important;
            }
        }

        &.selected,
        &.selected.odd,
        &.selected.even {
            >* {
                background-color: #7aa;
                box-shadow: none;
            }

            >.sorting_1,
            >.sorting_2,
            >.sorting_3 {
                box-shadow: none !important;
            }
        }

        &.selected+tr.selected>td {
            border-top-color: inherit;
        }
    }

    td.with-image {
        img {
            max-width: 1.5em;
            max-height: 1.5em;
            object-fit: contain;
        }
    }

}

ul.pagination {
    font-family: Sans;

    li {
        display: inline-block;
        margin: 0 .3em;
    }
}

.debug {
    display: v-bind(debugDisplay);
    border-top: solid 1px gray;
    box-sizing: border-box;
    font-weight: 300;
    font-size: 70%;
    color: #666;
    user-select: none;
    cursor: pointer;

    ::v-deep() {
        span:hover {
            background: pink;
        }
    }
}

.dt-container {

    --hi-color: red;
    --hi-width: 1px;

    &.highlight,
    ::v-deep(.highlight) {
        border: solid var(--hi-width) var(--hi-color) !important;
    }

    ::v-deep(tbody.highlight) {
        tr:first-child td {
            border-top: solid var(--hi-width) var(--hi-color) !important;
        }

        tr:last-child td {
            border-bottom: solid var(--hi-width) var(--hi-color) !important;
        }

        tr td:first-child {
            border-left: solid var(--hi-width) var(--hi-color) !important;
        }

        tr td:last-child {
            border-right: solid var(--hi-width) var(--hi-color) !important;
        }
    }

    ::v-deep() {

        th[data-type=BYTE],
        th[data-type=SHORT],
        th[data-type=INT],
        th[data-type=LONG],
        th[data-type=FLOAT],
        th[data-type=DOUBLE],
        th[data-type=BIG_INTEGER],
        th[data-type=BIG_DECIMAL] {
            text-align: center;
        }

        td[data-type=BYTE],
        td[data-type=SHORT],
        td[data-type=INT],
        td[data-type=LONG],
        td[data-type=FLOAT],
        td[data-type=DOUBLE],
        td[data-type=BIG_INTEGER],
        td[data-type=BIG_DECIMAL] {
            text-align: right;
        }
    }

}
</style>