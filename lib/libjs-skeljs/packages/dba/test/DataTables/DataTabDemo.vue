<script lang="ts">
import $ from 'jquery';
import "datatables.net";

import { onMounted } from 'vue';
import DataTable from '../../src/ui/table/DataTable.vue';
import { convertToDataRows, objv2Tab } from '../../src/ui/table/objconv';
import people from '../people-objv.js';

export const title = 'Table data modeled in DataTab type';

</script>

<script setup lang="ts">
var peopleTab = objv2Tab(people);

// Generate people-array.json:
let fields = peopleTab.columns.map(c => c.title);
let rows = convertToDataRows(fields, fields, people, (v) => v);
console.log(JSON.stringify({
    columns: fields,
    rows
}));

</script>

<template>
    <DataTable :data-tab="peopleTab" dom="ftip" caption="People Data">
        <th data-field="name">Name</th>
        <th data-field="sex">Gender</th>
        <th data-type="number" data-format="decimal2" data-field="age">Age</th>
        <th data-field="info.interest">Interests</th>
        <th data-field="info.hate">Hates</th>
    </DataTable>
</template>

<style lang="scss"></style>

<style scoped lang="scss"></style>