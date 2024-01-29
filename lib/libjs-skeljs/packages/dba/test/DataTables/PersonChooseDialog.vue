<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { EntityType } from '../../src/ui/table/types';

import { Person } from './Person';

import EntityChooseDialog from './EntityChooseDialog.vue';
import Editor from './PersonEditor.vue';

const model = defineModel();

interface Props {
    modal?: boolean | string
}

const props = withDefaults(defineProps<Props>(), {
    modal: true
});

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

// property shortcts

const type: EntityType = Person.TYPE;

const selection = ref();

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <EntityChooseDialog :type="Person.TYPE" :modal="modal" autoOpen="true">
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
