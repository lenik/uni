<script setup lang="ts">

import { computed, onMounted, ref } from 'vue';
import { convertToDataRows, objv2Tab } from '../../src/ui/table/objconv';
import { Selection, EntityType } from '../../src/ui/table/types';

import DataAdmin from '../../src/ui/table/DataAdmin.vue';
import Dialog from '@skeljs/core/src/ui/Dialog.vue';
import Editor from './PersonEditor.vue';

import people from '../people-objv.js';
import { Command } from '@skeljs/core/src/ui/types';

let people2 = [...people, ...people, ...people, ...people];

var peopleTab = ref(objv2Tab(people2));

// Generate people-array.json:
let fields = peopleTab.value.columns.map(c => c.title);

let rows = convertToDataRows(fields, fields, people, (v) => v);

const admin = ref<InstanceType<typeof DataAdmin>>();
const editor = ref<InstanceType<typeof Dialog>>();

const instance = ref<any>();

const type: EntityType = {
    name: 'Person',
    icon: 'fas-user-circle',
    label: 'Contact Person',
    description: 'A person is a living being that belongs to the species Homo sapiens, commonly known as humans. Humans are characterized by their ability to think, reason, and communicate using language. They have a complex biological makeup, including a highly developed brain, opposable thumbs, and a bipedal gait.',
};

function addNew(value) {
    console.log(1);
    let row = [value.name, value.sex, value.age, null, null];
    let dt = admin.value.dataTable;
    dt.dataTable.rows.add(row);
    return true;
}
function update(value) {
    let row = [value.name, value.sex, value.age, null, null];
    let dt = admin.value.dataTable;
    // dt.dataTable.rows.add(row);
    return false;
}

const tools = ref<Command[]>([
    {
        vPos: "top", pos: 'left', group: 'file', name: 'new',
        icon: 'fa-file', label: 'New',
        run: () => {
            instance.value = {};
            admin.value!.editor.open(addNew);
        }
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'open',
        icon: 'far-folder-open', label: 'Open',
        run: () => {
            admin.value!.editor.open(update);
        }
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'delete',
        icon: 'fa-trash', label: 'Delete',
        run: () => {
            admin.value!.dataTable.deleteSelection();
        }
    },

    {
        vPos: "top", pos: 'right', group: 'view', name: 'reload',
        icon: 'fa-sync', label: 'Reload',
    }, {
        vPos: "top", pos: 'right', group: 'view', name: 'edit',
        icon: 'fa-edit', label: 'Edit', type: 'toggle',
    }, {
        vPos: "top", pos: 'right', group: 'view', name: 'save',
        icon: 'fa-save', label: 'Save',
    },

]);

const selection = ref<Selection>();
const selectionText = computed(() => {
    let sel: Selection | undefined = selection.value;
    if (sel == null)
        return "unknown";
    if (sel.dataRow == null)
        return "nothing";

    let columns = sel.dataTable.columns().header().map(d => d.textContent).toArray();
    let sb = '';
    for (let i = 0; i < columns.length; i++) {
        let cell = sel.dataRow[i];
        if (cell == null) continue;
        if (sb.length) sb += ' ';
        sb += columns[i] + ":" + cell;
    }
    return sb;
});
const selectedRowCount = computed(() => {
    let sel: Selection | undefined = selection.value;
    if (sel == null) return 'not yet';
    let n = sel.rowIndexes.length;
    return n;
});

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
        pos: 'right', name: 'state',
        icon: 'fab-asymmetrik', label: 'State:',
        message: stateText
    },
]);

function onselect(sel: Selection) {
    selection.value = sel;
    if (sel.dataRow == null)
        instance.value = undefined;
    else {
        let [name, sex, age, interests, hates] = sel.dataRow!;
        let obj = { name, sex, age, interests, hates };
        instance.value = obj;
    }
}

</script>

<template>
    <DataAdmin ref="admin" :type="type" :tools="tools" :statuses="statuses" :data-tab="peopleTab" dom="ftip"
        v-model:instance="instance" @select="onselect">
        <template #columns>
            <th data-field="name">Name</th>
            <th data-field="sex">Gender</th>
            <th data-type="number" data-format="decimal2" data-field="age">Age</th>
            <th data-field="info.interest">Interests</th>
            <th data-field="info.hate">Hates</th>
        </template>

        <template #preview>
            <Editor class="editor" v-model="instance" />
        </template>

        <template #side-tools>
            Side Tools
        </template>

        <template #editor>
            <Editor class="editor" v-model="instance" />
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
    background: #fef;

    --color1: #ddd2;
    --color2: #fef2;
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