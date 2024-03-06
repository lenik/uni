<script lang="ts">
import { ref } from 'vue';
import { convertToDataRows, objv2Tab } from '../../src/ui/table/objconv';
import DataAdmin from '../../src/ui/table/DataAdmin.vue';
import Dialog from '@skeljs/core/src/ui/Dialog.vue';

import people from '../people-objv.js';
import { Command } from '@skeljs/core/src/ui/types';

export const title = 'Data Admin demo';
</script>

<script setup lang="ts">

var peopleTab = ref(objv2Tab(people));

// Generate people-array.json:
let fields = peopleTab.value.columns.map(c => c.title);

let rows = convertToDataRows(fields, fields, people, (v) => v);

const editDialog = ref<InstanceType<typeof Dialog>>();

const tools = ref<Command[]>([
    {
        vPos: "top", pos: 'left', group: 'file', name: 'new',
        icon: 'fa-file', label: 'New',
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'open',
        icon: 'far-folder-open', label: 'Open',
        run: () => {
            editDialog.value!.open();
        }
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'save',
        icon: 'fa-save', label: 'Save',
    },

    {
        vPos: "top", pos: 'right', group: 'media', name: 'play',
        icon: 'fas-play-circle', label: 'Play',
    }, {
        vPos: "top", pos: 'right', group: 'media', name: 'pause',
        icon: 'fas-pause-circle', label: 'Pause',
    }, {
        vPos: "top", pos: 'right', group: 'media', name: 'stop',
        icon: 'fas-stop-circle', label: 'Stop',
    },

    {
        vPos: "bottom", pos: 'right', group: 'volume', name: 'volume-up',
        icon: 'fa-volume-up', label: 'Louder',
    }, {
        vPos: "bottom", pos: 'right', group: 'volume', name: 'volume-down',
        icon: 'fa-volume-down', label: 'Quieter',
    }, {
        vPos: "bottom", pos: 'right', group: 'volume', name: 'mute',
        icon: 'fa-volume-mute', label: 'Mute',
    },

    {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'dial',
        icon: 'fa-phone', label: 'Dial',
    }, {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'sms',
        icon: 'fa-sms', label: 'Send SMS',
    }, {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'comments',
        icon: 'fa-comment-alt', label: 'Comments',
    }, {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'messages',
        icon: 'fa-comment-dots', label: 'Messenger',
    },

]);

const statuses = ref([
    {
        pos: 'left', name: 'status1',
        icon: 'fa-sms', label: 'Status OK'
    }, {
        pos: 'right', name: 'status2',
        icon: 'fa-sms', label: 'Status OK'
    }, {
        pos: 'right', name: 'status3',
        icon: 'fa-sms', label: 'Status OK'
    },
]);

</script>

<template>
    <DataAdmin :tools="tools" :statuses="statuses" :data-tab="peopleTab" dom="ftip" caption="People Data">
        <template #columns>
            <th data-field="name">Name</th>
            <th data-field="sex">Gender</th>
            <th data-field="age" data-type="INT" data-format="decimal2">Age</th>
            <th data-field="salary" data-type="INT" data-format="decimal2">Salary</th>
            <th data-field="info.interest">Interests</th>
            <th data-field="info.hate">Hates</th>
        </template>
        <template #side-tools> Side Tools </template>
        <template #preview> My preview </template>
        <template #editor>
            <Dialog ref="editDialog" modal="true" title="Edit Something" :cmds="{ ok: true, cancel: true }"> Hello,
                world. </Dialog>
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
}
</style>