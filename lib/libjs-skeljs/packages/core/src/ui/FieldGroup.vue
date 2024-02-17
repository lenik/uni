<script lang="ts">
import { onMounted, ref } from "vue";
import { } from "./types";

export interface Props {
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <slot></slot>
</template>

<style scoped lang="scss">
.field-group {
    padding: 0;
}
</style>
