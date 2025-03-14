<script setup lang="ts">

import { onMounted, ref } from "vue";
import { } from "./types";

const model = defineModel();

interface Props {
}

const props = withDefaults(defineProps<Props>(), {
});

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

// 
const rootElement = ref<HTMLElement>();

//
defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <div @click="emit('error', 'this is a test.')">
        </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}
</style>
