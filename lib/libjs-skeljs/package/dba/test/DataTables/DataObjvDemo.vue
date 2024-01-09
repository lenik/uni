<script setup lang="ts">

import DataTable from '../../src/component/table/DataTable.vue';
import { convertToDataRows, objv2Tab } from '../../src/component/table/objconv';
import people from '../people-objv.js';

</script>

<script lang="ts">
</script>

<template>
    <DataTable :data-objv="people" dom="ftip">
        <th data-field="name">Name</th>
        <th data-field="sex">Gender</th>
        <th data-type="number" data-format="decimal2" data-field="age">Age</th>
        <th data-field="info.interest">Interests</th>
        <th data-field="info.hate">Hates</th>
    </DataTable>
</template>

<style lang="scss"></style>

<style scoped lang="scss"></style>