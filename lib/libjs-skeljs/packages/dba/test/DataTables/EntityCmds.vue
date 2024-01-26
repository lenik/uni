<script setup lang="ts">

import $ from 'jquery';
import { computed, onMounted, ref } from 'vue';
import { convertToDataRows, objv2Tab } from '../../src/ui/table/objconv';
import { Selection, EntityType } from '../../src/ui/table/types';

import DataAdmin from '../../src/ui/table/DataAdmin.vue';
import Dialog from '@skeljs/core/src/ui/Dialog.vue';
import Editor from './PersonEditor.vue';

import people from '../people-objv.js';
import { Command } from '@skeljs/core/src/ui/types';

let people2 = [...people, ...people, ...people];

var peopleTab = ref(objv2Tab(people2));

// Generate people-array.json:
let fields = peopleTab.value.columns.map(c => c.title);

let rows = convertToDataRows(fields, fields, people, (v) => v);

const admin = ref<InstanceType<typeof DataAdmin>>();
const editor = ref<InstanceType<typeof Dialog>>();

interface RowInfo {
    index?: number
    // rowData?: any[]
    obj: any
}

const selectedRowInfo = ref<RowInfo>({
});

const type: EntityType = {
    name: 'Person',
    icon: 'fas-user-circle',
    label: 'Contact Person',
    description: 'A person is a living being that belongs to the species Homo sapiens, commonly known as humans. Humans are characterized by their ability to think, reason, and communicate using language. They have a complex biological makeup, including a highly developed brain, opposable thumbs, and a bipedal gait.',
};

function row2obj(row: any[]) {
    let [id, properties, label, description, gender, birthday] = row;
    return { id, properties, label, description, gender, birthday };
}

function obj2row(obj: any) {
    let { id, properties, label, description, gender, birthday } = obj;
    return [id, properties, label, description, gender, birthday];
}

function openNew() {
    let obj = {
        gender: 'm',
        age: 10,
    };
    selectedRowInfo.value = { obj };
    admin.value!.editor!.open(saveNew);
}

function saveNew(obj) {
    if (obj != null) {
        let row = obj2row(obj);
        let dt = admin.value!.dataTable;
        dt!.api!.row.add(row).draw()
            .nodes().to$().addClass('new');
    }
    focus();
    return true;
}

function openSelected() {
    admin.value!.editor!.open(saveSelected);
}

function saveSelected(value) {
    // assert value === selectedRowInfo.value
    if (value != null) {
        let info = selectedRowInfo.value!;
        let row = obj2row(value);
        let api = admin.value!.dataTable!.api!;
        api.row(info.index!).data(row).draw()
            .nodes().to$().addClass('dirty');
    }
    focus();
    return true;
}

function deleteSelection() {
    let before = rowNumInfo()!;
    admin.value?.dataTable?.deleteSelection();

    let pos = before.pos || 0;
    let after = rowNumInfo()!;
    let api = admin.value?.dataTable?.api;
    api?.row(after.nodes[pos]).select();
}

function reload() {

}
function toggleEditMode() {

}
function saveEdits() {


}
const tools = ref<Command[]>([
    {
        vPos: "top", pos: 'left', group: 'file', name: 'new',
        icon: 'fa-file', label: 'New',
        run: openNew
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'open',
        icon: 'far-folder-open', label: 'Open',
        run: openSelected
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'delete',
        icon: 'fa-trash', label: 'Delete',
        run: deleteSelection
    },

    {
        vPos: "top", pos: 'right', group: 'view', name: 'reload',
        icon: 'fa-sync', label: 'Reload',
        run: reload
    }, {
        vPos: "top", pos: 'right', group: 'view', name: 'edit',
        icon: 'fa-edit', label: 'Edit', type: 'toggle',
        run: toggleEditMode
    }, {
        vPos: "top", pos: 'right', group: 'view', name: 'save',
        icon: 'fa-save', label: 'Save',
        run: saveEdits
    },

]);

const selection = ref<Selection>();
const selectionText = computed(() => {
    let sel: Selection | undefined = selection.value;
    if (sel == null)
        return "unknown";
    if (sel.dataRow == null)
        return "nothing";

    let sb = '[' + sel.rowIndexes.join(', ') + '] ';

    let api = admin.value?.dataTable?.api!;

    let columns =
        api.columns().header().map(d => d.textContent).toArray();

    for (let i = 0; i < columns.length; i++) {
        let cell = sel.dataRow[i];
        if (cell == null) continue;
        if (i) sb += ' ';
        sb += columns[i] + ":" + cell;
    }
    return sb;
});
const selectedRowCount = computed(() => {
    let sel: Selection | undefined = selection.value;
    if (sel == null) return undefined;
    let n = sel.rowIndexes.length;
    return n;
});

const keyName = ref();
const stateText = ref('Ready');

const statuses = ref([
    {
        pos: 'left', name: 'selection',
        icon: 'fa-angle-double-right', label: 'Selection:',
        message: selectionText
    }, {
        pos: 'right', name: 'nRow',
        icon: 'fa-hashtag', label: 'Row Count:',
        message: selectedRowCount
    }, {
        pos: 'right', name: 'nChange',
        icon: 'fa-pen-nib', label: 'Changed Items:',
        message: 0
    }, {
        pos: 'right', name: 'key',
        icon: 'fas-keyboard', label: 'Key:',
        message: keyName
    }, {
        pos: 'right', name: 'state',
        icon: 'fab-asymmetrik', label: 'State:',
        message: stateText
    },
]);

function onselect(sel: Selection) {
    selection.value = sel;
    if (sel.dataRow == null)
        selectedRowInfo.value = {
            index: undefined,
            obj: undefined
        };
    else {
        let obj = row2obj(sel.dataRow!);
        selectedRowInfo.value = {
            index: sel.rowIndex,
            obj
        }
    }
}

function rowNumInfo() {
    let api = admin.value?.dataTable?.api;
    if (api == null) return null;
    let currentNode = api.row({ selected: true }).node();
    let nodes = api.rows({ order: 'applied' }).nodes();
    let current: number | undefined = undefined;
    if (currentNode != null)
        current = nodes.indexOf(currentNode);
    return {
        nodes: nodes,
        n: nodes.length,
        pos: current,
    }
}

function focus() {
    let elm = admin.value!.rootElement!;
    elm.focus();
}

onMounted(() => {
    let elm = admin.value!.rootElement!;
    focus();
    elm.addEventListener('keydown', (e: Event) => {
        if (e.target != elm) return false;

        let api = admin.value?.dataTable?.api;
        let next = 0;

        keyName.value = e.key;
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

            case 'ArrowUp':
            case 'ArrowDown':
            case 'Home':
            case 'End':
                if (api != null) {
                    let info = rowNumInfo()!;
                    let current = info.pos;
                    let next = 0;
                    if (current != null) {
                        switch (e.key) {
                            case 'ArrowUp': next = current - 1; break;
                            case 'ArrowDown': next = current + 1; break;
                            case 'Home': next = 0; break;
                            case 'End': next = info.n - 1; break;
                        }
                        // next = (next + info.n) % info.n;
                        if (next < 0) next = 0;
                        if (next >= info.n) next = info.n - 1;

                        api.row(info.nodes[current]).deselect();
                    }
                    api.row(info.nodes[next]).select();
                }
                break;

            case 'PageUp':
            case 'PageDown':
                if (api != null) {
                    let info = rowNumInfo()!;
                    let current = info.pos;
                    api.page(e.key == 'PageUp' ? 'previous' : 'next').draw(false);
                    info = rowNumInfo();
                    let pos = Math.min(current, info.nodes.length - 1);
                    api.row(info.nodes[pos]).select();
                }
                break;
        }
    }, true);
});

</script>

<template>
    <DataAdmin ref="admin" :type="type" :tools="tools" :statuses="statuses" dom="frtip" A:data-tab="peopleTab"
        fetch-size="10" processing=".processing" lily-url="http://localhost:2800/Person"
        v-model:instance="selectedRowInfo!.obj" @select="onselect">
        <template #columns>
            <th data-field="id">ID</th>
            <th data-field="properties" class="hidden">properties</th>
            <th data-field="label">Name</th>
            <th data-field="description">Description</th>
            <th data-field="gender">Gender</th>
            <th data-type="date" data-field="birthday">Birthday</th>
            <!-- <th data-field="properties.interest">Interests</th>
            <th data-field="properties.hate">Hates</th> -->
        </template>
        <template #preview>
            <Editor class="editor" v-model="selectedRowInfo!.obj" />
        </template>
        <template #side-tools> Side Tools</template>
        <template #editor>
            <Editor class="editor" v-model="selectedRowInfo!.obj" />
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

.workpane {
    padding: 1em;
    // background: #fef;

    --color1: #ddd3;
    --color2: #fef3;
    --width: 1px;
    --scale: 1;
    background: repeating-linear-gradient(-45deg,
            var(--color1),
            var(--color1) calc(var(--width) * var(--scale)),
            var(--color2) calc(var(--width) * var(--scale)),
            var(--color2) calc(10px * var(--scale)));
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

.editor {
    ::v-deep(.field-row) {
        margin: .5em 0;
    }
}
</style>