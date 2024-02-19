<script lang="ts">
import { onMounted } from 'vue';
export const title = 'Ajax with row=object';
</script>

<script setup lang="ts">
import DataTable from '../../src/ui/table/DataTable.vue';

</script>

<template class="k">
    <DataTable data-url="people-object.json" dom="ftip">
        <th data-field="name">Name</th>
        <th data-field="sex">Gender</th>
        <th data-field="age" data-type="number" data-format="decimal2">Age</th>
        <th data-field="info.interest">Interests</th>
        <th data-field="info.hate">Hates</th>
    </DataTable>
</template>

<style scoped lang="scss"></style>