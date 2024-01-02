<script setup lang="ts">

import { ref } from "vue';
"
import makeDataTable from "@/enh/datatable/dt-decl";
import formats from "@/enh/datatable/dt-formats";
import * as conv from "@/enh/datatable/objconv";
import { onMounted } from "vue";

interface DataObject {
    [key: string]: any
}

interface ColumnType {
    title: string
    type: string
    format: string
}

interface ColumnTypes {
    [key: string]: ColumnType
}

interface ColumnsData {
    columns: ColumnTypes
    data: any[][]
}

interface Props {

    objects?: DataObject[]
    columns?: ColumnTypes

    columnsData?: ColumnsData

    /**
     * when version changed, a full-update will be made.
     */
    version?: number

    watch?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    version: 0,
    watch: false
});

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
    var context = {
        getVar: (path) => {

        },
        getFormat: (path) => {
            
        }
    }
    makeDataTable(tableRef, context);
});
</script>

<template>
    <table v-ref="tableRef" class="datatable" :class="css">
        <thead>
            <slot>
            </slot>
        </thead>
        <tbody></tbody>
        <slot name="foot"></slot>
    </table>
</template>

<style lang="scss" scoped>
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