<script setup lang="ts">

import $ from 'jquery';
import { computed, onMounted, ref } from 'vue';
import { Api } from 'datatables.net';

import { EntityType } from '../../lily/entity';
import { Selection, ColumnType } from '../table/types';
import { Command, Status } from '@skeljs/core/src/ui/types';
import { showError } from '@skeljs/core/src/logging/api';
import { VarMap } from '@skeljs/core/src/lang/VarMap';

import DataAdmin from '../table/DataAdmin.vue';
import DataTable from '../table/DataTable.vue';
import { getDefaultCommands, getDefaultStatuses } from './defaults';
import { obj2Row } from '../table/objconv';

const model = defineModel<any>();

interface Props {
    url: string
    type: EntityType
}

const props = withDefaults(defineProps<Props>(), {
});

// computed properties

// app states

const stateText = ref('Ready');
const editMode = ref<boolean>(false);
const multiMode = ref<boolean>(false);
const pressedKeyName = ref();
const changeCount = ref<number>(0);

const selection = ref<Selection>();
const selectedRowCount = computed(() => selection.value?.size || 0);
const selectionText = computed(() => {
    let api = dataTableApi.value;
    if (api == null)
        return 'not ready';

    let sel: Selection | undefined = selection.value;
    if (sel == null)
        return "unknown";
    if (sel.empty)
        return "nothing";

    let sb = '[' + sel.dtIndexes.join(', ') + '] ';

    let columnHeaders =
        (api.columns() as any).header().map(d => d.textContent).toArray();

    for (let i = 0; i < columnHeaders.length; i++) {
        let cell = sel.firstRow[i];
        if (cell == null) continue;
        if (i) sb += ' ';
        sb += columnHeaders[i] + ":" + cell;
    }
    return sb;
});

// DOM references

const admin = ref<InstanceType<typeof DataAdmin>>();
const editorDialog = computed(() => admin.value?.editorDialog);

const dataTableComp = computed<undefined | InstanceType<typeof DataTable>>(() => admin.value?.dataTableComp);
const dataTableApi = computed<undefined | Api<any>>(() => dataTableComp.value?.api);
const columns = computed<undefined | ColumnType[]>(() => dataTableComp.value?.columns);

// utils

const provides = {
    reload, openNew, openSelected, deleteSelection, saveEdits,
    toggleMultiMode, multiMode, toggleEditMode, editMode,
    selectionText, selectedRowCount, changeCount, pressedKeyName, stateText,
};

const tools = ref<Command[]>(getDefaultCommands(provides));
const statuses = ref<Status[]>(getDefaultStatuses(provides));

defineExpose({
    ...provides,
    focus,
});

function focus() {
    admin.value?.rootElement?.focus();
}

function reload(callback?: any) {
    let api = dataTableApi.value!;
    (api.ajax as any).reloadSmooth(false, callback);
}

let defaultDepth = 2; // props.type.defaultDepth;
function openNew() {
    let params = new VarMap({
        depth: defaultDepth
    });

    let newUrl = props.url + "/new?" + params.toQueryString;

    $.ajax(newUrl)
        .done((data) => {
            model.value = data.data;
            editorDialog.value?.open(saveNew);
        });
}

function saveNew(obj) {
    if (obj != null) {
        let row = obj2Row(obj, columns.value!);
        let api = dataTableApi.value!;
        (api.row.add(row).draw() as any)
            .nodes().to$().addClass('new');
    }
    focus();
    return true;
}

function openSelected() {
    let params = new VarMap({
        depth: defaultDepth
    });

    let properties = props.type.properties;
    let pkProps = properties.filter(c => c.primaryKey);
    let pkCols = pkProps.map(p => ({ position: p.position, field: p.name } as ColumnType));
    let idVec = obj2Row(model.value, pkCols);
    let idPath = idVec.join('/');
    let fetchUrl = props.url + "/" + idPath + "?" + params.queryString;

    $.ajax(fetchUrl).done((data) => {
        console.log(data);
        model.value = data.data;
        editorDialog.value?.open(saveSelected);
    });
}

async function saveSelected(obj) {
    if (obj != null) {
        let updateUrl = props.url + "/save";
        let payload = JSON.stringify(model.value);
        console.log(payload);
        await $.ajax({
            url: updateUrl,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(model.value),
            type: "POST"
        }).done(function (data, status) {
            console.log(data);
            let row = obj2Row(obj, columns.value!);
            let api = dataTableApi.value!;

            let dtIndex = selection.value?.firstDtIndex;
            if (dtIndex != null)
                (api.row(dtIndex).data(row).draw() as any)
                    .nodes().to$().addClass('dirty');

            focus();
        }).fail((xhr, status, error) => {
            console.log(xhr);
            console.log(status);
            console.log(error);
        });
    }
    return true;
}

function deleteSelection() {
    let url = props.url;
    let id = model.value?.id;
    if (id == null) return;

    // url: dataHref + "/delete?id=" + ids.join(",")
    let deleteUrl = url + '/delete?id=' + id;

    let api = dataTableApi.value! as any;
    let info = api.rowNumInfo();
    let pos = info?.pos || 0;

    $.ajax({
        url: deleteUrl
    }).done(function (e) {
        reload(() => {
            info = api.rowNumInfo()!;
            pos = Math.min(pos, info.nodes.length - 1);
            api.row(info.nodes[pos]).select();
        });
    }).fail(function (xhr, status, err) {
        showError("Failed to delete: " + err);
    });
}

function toggleMultiMode() {
    console.log('multi ' + multiMode.value);
    let comp = dataTableComp.value!;
    comp.reset();
}

function toggleEditMode() {
    console.log('edit ' + editMode.value);
}

function saveEdits() {

}

// app behaviors

function extractRow(sel: Selection) {
    selection.value = sel;
    model.value = sel.toObject();
}

onMounted(() => {
    let elm = admin.value!.rootElement!;
    focus();
    elm.addEventListener('keydown', (e: any) => {
        if (e.target != elm) return false;

        let api = dataTableApi.value;
        let next = 0;

        pressedKeyName.value = e.key;
        if (e.ctrlKey)
            switch (e.key) {
                case 'd':
                    deleteSelection();
                    break;

                case 'r':
                    reload();
                    break;

                default:
                    break;
            }

        else
            switch (e.key) {
                case 'Insert':
                    openNew();
                    break;

                case 'Delete':
                    deleteSelection();
                    break;

                case 'e':
                    openSelected();
                    break;
                default:
                    return;
            }

        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();
        else
            e.cancelBubble = true;
    }, true);
});

</script>

<template>
    <DataAdmin ref="admin" :type="type" :tools="tools" :statuses="statuses" dom="frtip" :lily-url="url" fetch-size="10"
        processing=".processing" v-model:instance="model" :multi="multiMode" @select="extractRow">
        <template #columns>
            <slot name="columns"></slot>
        </template>
        <template #preview>
            <slot name="preview"></slot>
        </template>
        <template #side-tools> Side Tools</template>
        <template #editor>
            <slot name="editor"></slot>
        </template>
    </DataAdmin>
</template>

<style lang="scss">
.main {
    // border: solid 10px red;
    // background: #fef;
    padding: 0;
    margin: 0;

    display: flex;
    flex-direction: column;
}

.dataTable {
    background: #f8f8f8;
}

.entity-editor {
    .field-row {
        margin: .5em 0;
    }
}
</style>

<style scoped lang="scss">
.data-admin {
    flex: 1;
    margin: 0;
    // border: solid 2px red !important; 

    ::v-deep(.preview)>.content {
        padding: .5em 1em;
    }

}
</style>