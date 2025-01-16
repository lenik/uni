<script lang="ts">

import $ from 'jquery';
import { ref, onMounted, computed, inject } from "vue";
import { Api } from "datatables.net";

import { typeMap as baseTypeMap } from 'skel01-core/src/lang/baseinfo';
import { typeMap as timeTypeMap } from 'skel01-core/src/lang/time';
import { typeMap as basTypeMap } from 'skel01-core/src/lang/bas-info';

import { bool } from "skel01-core/src/ui/types";
import { baseName } from "skel01-core/src/io/url";
import { _throw, showError } from "skel01-core/src/logging/api";

import { ColumnType, DataTab, IDataTypeMap, Selection, SymbolCompileFunc } from "./types";
import { DataTableInstance, useAjaxDataTable, useDataTable } from "./DataTable-apply";
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
    select: [selection: Selection, event: Event]
}>();

// computed properties

const typeMap = computed(() => props.typeMap || defaultTypeMap);

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
    emit('select', sel, e);
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
@use 'DataTable.scss';
</style>