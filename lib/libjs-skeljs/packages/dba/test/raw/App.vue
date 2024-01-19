<script setup lang="ts">

import { onMounted } from 'vue';

import DataTable from "datatables.net";
import 'datatables.net-dt/css/jquery.dataTables.css';

onMounted(() => {
    new DataTable('#test', {
        columns: [
            { title: 'name' },
            { title: 'sex' },
            { title: 'age' },
        ],
        data: [
            ['foo', 'm', 116],
            ['bar', 'f', 32],
            ['cat', 'f', 26],
        ]
    });
});
</script>

<template>
    <h1>Raw datatable</h1>

    <div>
        <table id="test" class="tabledatatable display">
            <thead>
                <tr>
                    <th>name</th>
                    <th>sex</th>
                    <th>age</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>
</template>

<style lang="scss" scoped></style>