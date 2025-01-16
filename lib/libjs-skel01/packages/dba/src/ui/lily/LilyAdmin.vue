<script lang="ts">
import $ from 'jquery';
import { computed, inject, onMounted, ref } from 'vue';
import { Api } from 'datatables.net';

import IEntityType from '../../net/bodz/lily/entity/IEntityType';
import EntityType from '../../net/bodz/lily/entity/EntityType';
import { Selection, ColumnType } from '../table/types';
import { SERVER_URL } from './context';

import { Command, DialogOpenOptions, Status } from 'skel01-core/src/ui/types';
import { showError, _throw } from 'skel01-core/src/logging/api';
import { VarMap } from 'skel01-core/src/lang/VarMap';
import { wireUp, flatten } from 'skel01-core/src/lang/json';
import { QueryString } from 'skel01-core/src/cgi/QueryString';

export interface Props {
    type: EntityType
    serverUrl?: string
    url?: string
}
</script>

<script setup lang="ts">
import DataAdmin from '../table/DataAdmin.vue';
import DataTable from '../table/DataTable.vue';
import { getDefaultCommands, getDefaultStatuses } from './defaults';
import { obj2Row } from '../table/objconv';

const model = defineModel<any>();

const props = withDefaults(defineProps<Props>(), {
});

// computed properties

const serverUrl = inject<string>(SERVER_URL)!;
const _url = computed(() => {
    if (props.url != null)
        return props.url;

    let svr = props.serverUrl || serverUrl;
    if (svr != null)
        return svr + "/" + props.type.simpleName;

    throw new Error("either lily-url or server-url have to be specified.");
});

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

function getIdPath(instance: any) {
    let properties = props.type.properties;
    let pkProps = properties.filter(c => c.primaryKey);
    let pkCols = pkProps.map(p => ({ position: p.position, field: p.name } as ColumnType));
    let idVec = obj2Row(instance, pkCols);
    let idPath = idVec.join('/');
    return idPath;
}

let defaultDepth = 2; // props.type.defaultDepth;
function openNew(options?: DialogOpenOptions) {
    let params = new VarMap({
        depth: defaultDepth
    });

    let newUrl = _url.value + "/new?" + params.queryString;

    $.ajax(newUrl)
        .done((data) => {
            let wired = wireUp(data);
            let parsed = props.type.fromJson(wired);
            model.value = parsed;
            editorDialog.value?.open({
                onselect: saveNew,
                onclose: () => {
                    // options?.onclose
                    admin.value?.rootElement?.focus();
                }
            });
        });
}

function openSelected(options?: DialogOpenOptions) {
    let params = new VarMap({
        depth: defaultDepth
    });
    let idPath = getIdPath(model.value);
    let fetchUrl = _url.value + "/" + idPath + "?" + params.queryString;

    $.ajax(fetchUrl).done((data) => {
        let wired = wireUp(data);
        let parsed = props.type.fromJson(wired);
        console.log('openSelected', parsed);
        model.value = parsed;
        editorDialog.value?.open({
            onselect: saveSelected,
            onclose: () => {
                // if (options?.onclose != null) ;
                admin.value?.rootElement?.focus();
            }
        });
    });
}

async function saveNew() {
    let saveUrl = _url.value + "/saveNew";
    await _save(saveUrl, model.value, true);

    let api = dataTableApi.value!;
    let row = obj2Row(model.value, columns.value!);

    let newRow = api.row.add(row) as any;
    newRow.draw();
    try {
        newRow.nodes().to$().addClass('new');
    } catch (err) {
        console.error('error get newRow.nodes()', err);
        (api.ajax as any).reloadSmooth(false, () => {
            let info = (api as any).rowNumInfo()!;
            let pos = 0;
            // pos = Math.min(pos, info.nodes.length - 1);
            // api.row(info.nodes[pos]).select();
        });
    }
    return true;
}

async function saveSelected(obj: any) {
    if (obj == null)
        // unexpected. do nothing
        return true;

    let idPath = getIdPath(obj);
    let updateUrl = _url.value + "/" + idPath;

    await _save(updateUrl, model.value, false);

    let dtIndex = selection.value?.firstDtIndex;
    if (dtIndex != null) {
        let api = dataTableApi.value!;
        let row = obj2Row(obj, columns.value!);

        let updateRow = api.row(dtIndex).data(row) as any;
        updateRow.draw();
        if (updateRow.nodes != null)
            updateRow.nodes().to$().addClass('dirty');
        else if (updateRow.node != null)
            $(updateRow.node()).addClass('dirty');
    }

    return true;
}

function addParamToUrl(url: string, name: string, value: string) {
    let ques = url.lastIndexOf('?');
    if (ques != -1) {
        // TODO remove same named param.
        url += '&';
    } else {
        url += '?';
    }
    url += 'name=' + value;
    return url;
}

async function _save(url: string, obj: any, createNew: boolean) {
    // if (createNew)
    //     url = addParamToUrl(url, 'saveNew', 'true');
    let jv = props.type.toJson(obj);
    console.log('toJson/save', jv);

    let payload;
    try {
        payload = JSON.stringify(jv);
    } catch (e) {
        // Uncaught TypeError: Converting circular structure to JSON
        let flat = flatten(jv);
        payload = JSON.stringify(flat);
    }
    await $.ajax({
        url: url,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: payload,
        type: createNew ? "POST" : "PATCH"
    }).done(function (data, status) {
        focus();
    }).fail((xhr, status, error) => {
        console.log(error);
    });
    return true;
}

function deleteSelection() {
    let id = model.value?.id;
    if (id == null) return;

    // url: dataHref + "/delete?id=" + ids.join(",")
    let deleteUrl = _url.value + '/delete?id=' + id;

    let api = dataTableApi.value! as any;
    let info = api.rowNumInfo();
    let pos = info?.pos || 0;

    $.ajax({
        url: deleteUrl,
        method: 'DELETE'
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
    <DataAdmin ref="admin" :type="type" :tools="tools" :statuses="statuses" dom="frtip" :lily-url="_url" fetch-size="10"
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

        textarea {
            flex: 1;
        }

        .jsoneditor-wrapper {
            flex: 1;
        }
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
