<script setup lang="ts">

import { onMounted, ref } from "vue";
import { DialogSelectCallback } from "@skeljs/core/src/ui/types";

import { EntityType } from '../../entity/EntityType';
import EntityChooseDialog from '../../../../../ui/lily/EntityChooseDialog.vue';

import { Person } from './Person';

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

// property shortcuts

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
