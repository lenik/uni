<script setup lang="ts">

import $, { data } from "jquery";

import { ref, onMounted, computed } from "vue";
import { getCurrentInstance } from 'vue';

import { showError } from "@skeljs/core/src/logging/api";

import type { DataTab, SymbolCompileFunc } from "./types";
import formats from "./formats";
import { useAjaxDataTable, useDataTable } from "./apply";
import { objv2Tab } from "./objconv";

interface Props {

    caption?: string
    captionPosition?: 'top' | 'bottom' | 'both'

    dataObjv?: any[]

    // columns?: ColumnType[]
    dataTab?: DataTab

    compile?: SymbolCompileFunc

    config?: any

    /**
     * when version changed, a full-update will be made.
     */
    version?: number

    watch?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    captionPosition: 'bottom',
    compile: formats,
    version: 0,
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

defineOptions({
    inheritAttrs: false
})

const css = [
    "table",
    "table-striped",
    "table-hover",
    "table-condensed",
    "dataTable",
    "table-responsive",
];

const tableRef = ref(null);

onMounted(() => {
    let cur = getCurrentInstance();

    let table: HTMLElement = tableRef.value!;
    // let $table = $(table);
    // console.log($table.dataTable);

    if (props.dataTab != null) {
        useDataTable(tableRef.value!, props.config, props.compile, () => props.dataTab);
        return;
    }

    if (props.dataObjv != null) {
        let fetch = () => {
            let dataTab = objv2Tab(props.dataObjv!);
            return dataTab;
        };
        useDataTable(tableRef.value!, props.config, props.compile, fetch);
        return;
    }

    let dataUrl = table.getAttribute('data-url');
    if (dataUrl != null) {
        useAjaxDataTable(tableRef.value!, props.config, props.compile);
        return;
    }

    throw 'invalid use of <DataTable>';
    showError('invalid use of <DataTable>');
});

</script>

<template>
    <div class="caption" v-if="captionAtTop">{{ caption }}</div>
    <table ref="tableRef" v-once class="datatable" :class="css" v-bind="$attrs">
        <thead>
            <slot>
            </slot>
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

.dataTables_wrapper {
    .dataTable {

        tbody {
            tr {
                &:hover {
                    background-color: #dee;
                }

                &.selected {
                    background-color: #7aa !important;
                }
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
}
</style>