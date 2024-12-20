<script setup lang="ts">

import { ref, watch } from 'vue';

interface Props {
    name: string
    selection: string
}

const data = defineModel('data');
const props = defineProps<Props>();
const emit = defineEmits<{
    (e: 'enter', name: string, data: any): boolean
    (e: 'leave', name: string, data: any): boolean
}>();

const rootElement = ref();

watch(() => props.selection, async (toVal, fromVal) => {
    let name = props.name;
    // console.log(`[${name} detected: view changed from ${fromVal} to ${toVal}`);

    if (fromVal == name) {
        emit('leave', name, data);
    }

    if (toVal == name) {
        emit('enter', name, data);
    }
    return true;
});

</script>

<template>
    <div :ref="rootElement" :id="'v-' + name" class="view" :class="{ selected: selection == name }">
        <slot></slot>
    </div>
</template>

<style lang="scss"></style>