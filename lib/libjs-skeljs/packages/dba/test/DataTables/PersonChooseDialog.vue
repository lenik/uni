<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { EntityType } from '../../src/ui/table/types';

import { Person } from './Person';

import EntityChooseDialog from './EntityChooseDialog.vue';
import Editor from './PersonEditor.vue';
import { DialogSelectCallback } from "@skeljs/core/src/ui/types";

const model = defineModel();

interface Props {
    modal?: boolean | string
}

const props = withDefaults(defineProps<Props>(), {
    modal: true
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcts

const type: EntityType = Person.TYPE;

const selection = ref();
const entityChooseDialog = ref<undefined | InstanceType<typeof EntityChooseDialog>>();

defineExpose({ open });

function open(callback?: DialogSelectCallback) {
    entityChooseDialog.value?.open(callback);
}

onMounted(() => {
});
</script>

<template>
    <EntityChooseDialog ref="entityChooseDialog" :type="Person.TYPE" :modal="modal">
        <th data-field="id">ID</th>
        <th data-field="properties" class="hidden">properties</th>
        <th data-field="label">Name</th>
        <th data-field="description">Description</th>
        <th data-field="gender">Gender</th>
        <th data-type="date" data-field="birthday">Birthday</th>
    </EntityChooseDialog>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}
</style>
