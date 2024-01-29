<script setup lang="ts">

import { computed, onMounted, ref } from 'vue';
import { EntityType } from '../../src/ui/table/types';

import LilyAdmin from '../../src/ui/table/LilyAdmin.vue';
import Editor from './PersonEditor.vue';

import { } from '@skeljs/core/src/logging/api';

interface Props {
}

const props = withDefaults(defineProps<Props>(), {
});

// let people2 = [...people, ...people, ...people];

// var peopleTab = ref(objv2Tab(people2));

// // Generate people-array.json:
// let fields = peopleTab.value.columns.map(c => c.title);

// let rows = convertToDataRows(fields, fields, people, (v) => v);

const admin = ref<InstanceType<typeof LilyAdmin>>();
const editModel = ref<any>({});

// const editModel = computed<any>(() => admin.value?.editModel);

const type: EntityType = {
    name: 'Person',
    icon: 'fas-user-circle',
    label: 'Contact Person',
    description: 'A person is a living being that belongs to the species Homo sapiens, commonly known as humans. Humans are characterized by their ability to think, reason, and communicate using language. They have a complex biological makeup, including a highly developed brain, opposable thumbs, and a bipedal gait.',
};

// function row2obj(row: any[]) {
//     let [id, properties, label, description, gender, birthday] = row;
//     return { id, properties, label, description, gender, birthday };
// }

// function obj2row(obj: any) {
//     let { id, properties, label, description, gender, birthday } = obj;
//     return [id, properties, label, description, gender, birthday];
// }

onMounted(() => {
});

</script>

<template>
    <LilyAdmin ref="admin" url="http://localhost:2800/Person" :type="type" v-model="editModel">
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
            <Editor class="editor" v-model="editModel" />
        </template>
        <template #side-tools> Side Tools</template>
        <template #editor>
            <Editor class="editor" v-model="editModel" />
        </template>
    </LilyAdmin>
</template>

<style lang="scss"></style>

<style scoped lang="scss">
.lily-admin {}
</style>