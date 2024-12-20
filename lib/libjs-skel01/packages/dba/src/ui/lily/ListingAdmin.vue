<script lang="ts">
import $ from 'jquery';
import { computed, inject, onMounted, ref } from 'vue';

import type { int, long } from 'skel01-core/src/lang/basetype';
import { wireUp } from 'skel01-core/src/lang/json';
import { showError } from 'skel01-core/src/logging/api';

import EntityType from '../../net/bodz/lily/entity/EntityType';
import { SERVER_URL } from './context';

import { obj2Row, row2Obj } from '../table/objconv';
import type { ColumnType } from '../table/types';

export interface Props {
    type: EntityType
    serverUrl?: string
    url?: string
    columns?: string[]
    pageSize?: number
    page?: number // 1-based
    order?: string
    search?: any
    importOptions?: any
}

export interface IDataView {
    range: any[]
    totalRows: long
    offset: long
    pageSize: long
    pageCount: long
    page: long
}

</script>

<script setup lang="ts">
import Dialog from 'skel01-core/src/ui/Dialog.vue';
import ImportPanel from './ImportPanel.vue';

const model = defineModel<IDataView>();

const props = withDefaults(defineProps<Props>(), {
    page: 0,
    order: "lastmod desc"
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const serverUrl = inject<string>(SERVER_URL)!;
const entityUrl = computed(() => {
    if (props.url != null)
        return props.url;

    let svr = props.serverUrl || serverUrl;
    if (svr != null)
        return svr + "/" + props.type.simpleName;

    throw new Error("either lily-url or server-url have to be specified.");
});
const listUrl = computed(() => entityUrl.value + '/__data_' + props.type.simpleName);
const entityClass = computed(() => props.type.name);

// app states

const stateText = ref('Ready');
const loading = ref<boolean>();
const dataColumns = ref<string[]>([]);
const dataObjects = ref<any[]>([]);
const totalRows = ref<long>(0);
const realPageSize = computed<int>(() => props.pageSize || totalRows.value);
const pageCount = computed<long>(() => {
    if (realPageSize.value == 0)
        return 1;
    else
        return Math.ceil(totalRows.value / realPageSize.value);
});
const currentPage = ref<long>(1);

const radius = 2;
const neighbourStart = computed<long>(() => Math.max(1, currentPage.value - radius));
const neighbourEnd = computed<long>(() => Math.min(pageCount.value, currentPage.value + radius));
const neighbours = computed<long[]>(() =>
    Array(neighbourEnd.value - neighbourStart.value + 1)
        .fill(0).map((a, i) => i + neighbourStart.value));

const importData = ref();

// DOM references

const rootElement = ref<HTMLElement>();
const importDialog = ref<InstanceType<typeof Dialog>>();
const importPanel = ref<InstanceType<typeof ImportPanel>>();
const dialogName = "importDialog";

// methods

defineExpose({ update });

function update() {
}

function getIdPath(instance: any) {
    let properties = props.type.properties;
    let pkProps = properties.filter(c => c.primaryKey);
    let pkCols = pkProps.map(p => ({ position: p.position, field: p.name } as ColumnType));
    let idVec = obj2Row(instance, pkCols);
    let idPath = idVec.join('/');
    return idPath;
}

function goPage(n: int) {
    reload(n);
}
function goFirst() {
    goPage(1);
}
function goPrevious() {
    goPage(Math.max(1, currentPage.value - 1));
}
function goNext() {
    goPage(Math.min(pageCount.value, currentPage.value + 1));
}
function goLast() {
    goPage(pageCount.value);
}

function reload(page?: int) {
    let query: any = {
        row: "array", // or "object"
        counting: true, // want total count
    };

    let columnNames = props.columns;
    if (columnNames != null) {
        query.columns = columnNames.join(",");
    }

    if (props.order != null)
        query["order"] = props.order;

    let page_z = (page || currentPage.value) - 1;
    if (page_z < 0) page_z = 0;

    if (props.pageSize != null) {
        query["pageIndex"] = props.pageSize * page_z;
        query["pageSize"] = props.pageSize;
    }

    if (props.search != null)
        Object.assign(query, props.search);

    loading.value = true;

    $.ajax({
        url: listUrl.value,
        data: query,
        method: "GET"
    }).done(function (data: any) {
        if (typeof (data) != "object" || !Array.isArray(data.rows)) {
            showError("Invalid response: " + data);
            return;
        }

        data = wireUp(data);
        totalRows.value = data.totalCount;
        dataColumns.value = data.columns;

        let anyList: any[] = data.rows!;
        let objs: any[] = [];

        if (anyList.length) {
            let headRow = anyList[0];
            let isArray = Array.isArray(headRow);
            for (let row of anyList) {
                let jo: any;
                if (isArray) {
                    jo = row2Obj(row, data.columns);
                } else {
                    jo = row;
                }
                let obj = props.type.fromJson(jo);
                objs.push(obj);
            }
        }
        dataObjects.value = objs;
        if (page != null)
            currentPage.value = page;
        loading.value = false;

        model.value = {
            range: objs,
            totalRows: totalRows.value,
            offset: realPageSize.value * page_z,
            pageSize: realPageSize.value,
            pageCount: pageCount.value,
            page: page_z + 1
        };
    }).fail(function (xhr: any, status: any, err) {
        showError("Failed to query index data: " + err);
    });
}

function showImportDialog() {
    importPanel.value?.reset();
    importDialog.value?.open();
}

function onImported(result: any) {
    reload();
}

onMounted(() => {
    reload(props.page);
});

</script>

<template>
    <div class="listing-admin" ref="rootElement">
        <div class="loading" v-if="loading">Loading...</div>
        <ul v-else>
            <li class="data-row" v-for="obj in  dataObjects " :key="obj.id" :data-id="obj.id">
                <slot v-bind="{ o: obj, ...obj }"></slot>
            </li>
            <li class="pagination">
                <a @click="reload()">Reload</a>
                <a @click="goFirst()">First</a>
                <a @click="goPrevious()">Previous</a>
                <span v-if="neighbourStart != 1">...</span>
                <a v-for="page in neighbours" :key="page" :class="{ selected: page == currentPage }"
                    @click="goPage(page)">{{ page }}</a>
                <span v-if="neighbourEnd != pageCount">...</span>
                <a @click="goNext()">Next</a>
                <a @click="goLast()">Last</a>
            </li>
            <li class="tools">
                <a @click="showImportDialog()">Import from Excel/CSV...</a>
            </li>
        </ul>
        <Dialog ref="importDialog" class="import-dialog" :name="dialogName" v-model="importData" modal
            title="Import from Excel/CSV..." height="25em" :cmds="{ close: true }">
            <ImportPanel ref="importPanel" :type="props.type" :options="importOptions" @done="onImported">

                <template #options>
                    <slot name="import-options"></slot>
                </template>

                <template #item="{ o }">
                    <slot name="import-item" v-bind="{ o, ...o }"></slot>
                </template>

                <template #error="{ e }">
                    <slot name="import-error" v-bind="{ e }"></slot>
                </template>
            </ImportPanel>
        </Dialog>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}

.tools,
.pagination {
    >* {
        cursor: pointer;
        user-select: none;

        &:not(:first-child) {
            margin-left: .5em;
        }
    }

    >a {
        color: blue;

        &:before {
            content: "[";
        }

        &:after {
            content: "]";
        }

        &:before,
        &:after {
            color: #888;
        }

        &.selected {
            font-weight: bold;
            background-color: #eee;
        }
    }
}
</style>
