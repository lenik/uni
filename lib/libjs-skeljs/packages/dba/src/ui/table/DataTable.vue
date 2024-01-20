<script setup lang="ts">

import { ref, onMounted, computed, getCurrentInstance } from "vue";

import { showError } from "@skeljs/core/src/logging/api";

import type { DataTab, SymbolCompileFunc } from "./types";
import { Selection } from "./types";

import formats from "./formats";
import { useAjaxDataTable, useDataTable } from "./apply";
import { objv2Tab } from "./objconv";
import { baseName } from "@skeljs/core/src/io/url";
import { bool } from "@skeljs/core/src/ui/types";
import { Api } from "datatables.net";
import { keepSelection } from "./utils";

interface Props {

    caption?: string
    captionPosition?: 'top' | 'bottom' | 'both'

    dataObjv?: any[]

    // columns?: ColumnType[]
    dataTab?: DataTab

    dataUrl?: string
    entityUrl?: string // lily-specific

    compile?: SymbolCompileFunc

    config?: any
    multi?: boolean | string

    /**
     * when version changed, a full-update will be made.
     */
    version?: number

    hBand?: boolean
    vBand?: boolean

    watch?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    captionPosition: 'bottom',
    compile: formats,
    multi: false,
    version: 0,
    hBand: true,
    vBand: false,
    watch: false
});

const captionAtTop = computed(() => props.caption != null
    && (props.captionPosition == 'top'
        || props.captionPosition == 'both'
    ));
const captionAtBottom = computed(() => props.caption != null
    && (props.captionPosition == 'bottom'
        || props.captionPosition == 'both'
    ));

const selectedRows = computed(() => {
    let dt = api.value;
    return dt.rows({ selected: true }).data();
});
const selectedRowIndexes = computed(() => {
    let dt = api.value;
    return dt.rows({ selected: true }).index();
});

defineOptions({
    inheritAttrs: false
})

const css = [
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

interface Emits {
    (e: 'select', selection: Selection): void
}

const emit = defineEmits<Emits>();

onMounted(() => {
    let cur = getCurrentInstance();

    let table: HTMLElement = tableRef.value!;

    let config = props.config || {};

    let multiSelect = bool(props.multi);
    if (multiSelect)
        config.select = {
            info: true,
            style: 'multi+shift',
            selector: 'td',
        };

    let dt: Api<any> | undefined;

    if (props.dataTab != null) {
        dt = useDataTable(tableRef.value!, config, props.compile, () => props.dataTab);
    }

    else if (props.dataObjv != null) {
        let fetch = () => {
            let dataTab = objv2Tab(props.dataObjv!);
            return dataTab;
        };
        dt = useDataTable(tableRef.value!, config, props.compile, fetch);
    }

    else {
        let dataUrl = props.dataUrl;
        if (dataUrl == null && props.entityUrl != null) {
            let url = props.entityUrl;
            if (url.endsWith("/"))
                url = url.substring(0, url.length - 1);
            let classHint = baseName(url);
            dataUrl = url + "/__data__" + classHint;
        }

        if (dataUrl != null) {
            dt = useAjaxDataTable(tableRef.value!, config, props.compile);
        }
    }

    if (dt == null) {
        showError('invalid use of <DataTable>');
        throw 'invalid use of <DataTable>';
    }

    api.value = dt;

    function selectionChange(e: Event, dt: Api<any>, type: string, indexes: number[], select: boolean) {
        let selectedRows = dt.rows({ selected: true }).data().toArray() as any[][];
        let selectedIndexes = dt.rows({ selected: true }).indexes().toArray() as number[];
        let selection: Selection = {
            select: select,
            event: e,
            dataTable: dt,
            dataRows: selectedRows,
            dataRow: selectedRows[0],
            rowIndexes: selectedIndexes,
            rowIndex: selectedIndexes[0],
        };
        emit('select', selection);
    }

    if (!multiSelect)
        keepSelection(dt, selectionChange);

    dt.on('select', (e, dt, type, indexes) => selectionChange(e, dt, type, indexes, true));
    // dt.on('deselect', (e, dt, type, indexes) => selectionChange(e, dt, type, indexes, true));

    // dt.on('click', 'tbody td:not(:first-child)', function (e) {
    //     editor.inline(this);
    // });
});

function deleteSelection() {
    let dt = api.value;
    dt!.rows({ selected: true }).remove().draw(false);
}

defineExpose({ api: api, deleteSelection });

</script>

<template>
    <div class="caption" v-if="captionAtTop">{{ caption }}</div>
    <table ref="tableRef" class="dataTable" :class="css" v-bind="$attrs">
        <thead>
            <tr>
                <slot>
                </slot>
            </tr>
        </thead>
        <tbody>
            <slot name="body"></slot>
        </tbody>
        <slot name="foot"></slot>
    </table>
    <div class="caption" v-if="captionAtBottom">{{ caption }}</div>
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

.dataTables_wrapper {}

// v-deep() since the content are dynamically changed.
table.dataTable::v-deep() {
    >tbody>tr {
        &:hover {
            background-color: #dee;
        }

        &.selected {
            background-color: #7aa !important;
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
</style>