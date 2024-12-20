<script lang="ts">
import { INT, STRING } from '@skel01/core/src/lang/baseinfo';

export const title = 'Ajax with row=array';
</script>

<script setup lang="ts">
import DataTable from '../../src/ui/table/DataTable.vue';

</script>

<template>
    <DataTable data-url="people-array.json" dom="ftip">
        <th data-field="name">Name</th>
        <th data-field="sex">Gender</th>
        <th data-field="age" data-type="INT" data-format="decimal2">Age</th>
        <th data-field="salary" data-type="INT">Salary</th>
        <th data-field="info.interest">Interests</th>
        <th data-field="info.hate">Hates</th>
    </DataTable>
</template>

<style lang="scss">
</style>

<style scoped lang="scss">
.dt-container {
    flex: 1;
}
</style>