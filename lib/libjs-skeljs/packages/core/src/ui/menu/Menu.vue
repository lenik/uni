<script lang="ts">
const title = "component Menu";

import { onMounted, ref } from "vue";

export interface Props {
}
</script>

<script setup lang="ts">
import Icon from "@skeljs/core/src/ui/Icon.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

// app state

// DOM references
const rootElement = ref<HTMLElement>();

// methods
defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <div @click="emit('error', 'this is a test.')">
            Emit an error.
        </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}
</style>
