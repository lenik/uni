<script setup lang="ts">

import $ from 'jquery';
import { computed, onMounted, ref } from 'vue';
import { Api } from 'datatables.net';

import { Selection, EntityType, ColumnType } from '../table/types';
import { Command, Status } from '@skeljs/core/src/ui/types';
import { showError } from '@skeljs/core/src/logging/api';
import { slowly } from '@skeljs/core/src/skel/waitbox';

import Dialog from '@skeljs/core/src/ui/Dialog.vue';
import DataAdmin from './DataAdmin.vue';
import DataTable from './DataTable.vue';
import { getDefaultCommands, getDefaultStatuses } from './defaults';
import { row2Obj, obj2Row } from './objconv';

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

function openNew() {
    let obj = {
        gender: 'm',
        age: 10,
    };
    model.value = obj;
    editorDialog.value?.open(saveNew);
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
    editorDialog.value?.open(saveSelected);
}

function saveSelected(obj) {
    if (obj != null) {
        let row = obj2Row(obj, columns.value!);
        let api = dataTableApi.value!;

        let dtIndex = selection.value?.firstDtIndex;
        if (dtIndex != null)
            (api.row(dtIndex).data(row).draw() as any)
                .nodes().to$().addClass('dirty');
    }
    focus();
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
    if (sel.firstRow == null)
        model.value = {};
    else {
        model.value = row2Obj(sel.firstRow, columns.value!);
    }
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

::v-deep(.editor) {
    .field-row {
        margin: .5em 0;
    }
}
</style>