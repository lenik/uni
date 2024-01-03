<script setup lang="ts">

import { ref } from 'vue';

import ModelC from 'skeljs-core/src/component/layout/ModelC.vue';
import SiteBar from 'skeljs-core/src/component/menu/SiteBar.vue';
import ProjectInfo from 'skeljs-core/src/component/demo/ProjectInfo.vue';
import NotImpl from 'skeljs-core/src/component/demo/NotImpl.vue';
import TabViews from 'skeljs-core/src/component/layout/TabViews.vue';
import View from 'skeljs-core/src/component/layout/View.vue';

import * as conv from "../../src/component/table/objconv.ts";

</script>

<script lang="ts">
var people = [{
    name: 'Tom',
    age: 20,
    sex: 'm'
}, {
    name: 'Lucy',
    age: 16,
    sex: 'f'
}, {
    name: 'Lily',
    age: 12,
    sex: 'f'
}];
var peopleTab = conv.objv2Tab(people);

var win: any = window;
win.peopleTab = peopleTab;

</script>

<template>
    <ModelC>
        <template #project-info>
            <ProjectInfo label="Layout Test Page">
                An example page using skeljs-dba DataBase component.
            </ProjectInfo>
        </template>

        <DataTable data-var="peopleTab" dom="t">
            <thead>
                <th data-field="name">Name</th>
                <th data-field="gender">Gender</th>
                <th data-type="number" data-format="decimal2" data-field="age">Age</th>
            </thead>
        </DataTable>
    </ModelC>
</template>

<style lang="scss" scoped></style>