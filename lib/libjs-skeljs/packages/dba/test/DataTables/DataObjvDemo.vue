<script lang="ts">
import DataTable from '../../src/ui/table/DataTable.vue';
import people from '../people-objv.js';
import { Converter } from '../objv2ddl';

export const title = 'Data row by object[]';
</script>

<script setup lang="ts">
function plusYears(date: Date, n: number) {
    let year = date.getFullYear();
    let newDate = new Date(date);
    newDate.setFullYear(year + n);
    return newDate;
}

let personList = people.map(a => ({
    label: a.name,
    description: a.name + ' converted from people-objv',
    gender: a.sex,
    birthday: plusYears(new Date(), -1),
    props: JSON.stringify(a.info),
}));

let ddl = new Converter({
    birthday: {
        format: (d: any) => "'" + (d as Date).toDateString() + "'",
    }
}).toInsert('person', ...personList);

</script>

<template>
    <DataTable :data-objv="people" dom="ftip">
        <th data-field="name">Name</th>
        <th data-field="sex">Gender</th>
        <th data-type="number" data-format="decimal2" data-field="age">Age</th>
        <th data-field="info.interest" data-order="0">Interests</th>
        <th data-field="info.hate">Hates</th>
    </DataTable>
</template>

<style lang="scss"></style>

<style scoped lang="scss">
.dt-container {
    flex: 1;
}
</style>