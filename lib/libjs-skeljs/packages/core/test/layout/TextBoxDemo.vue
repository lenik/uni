<script lang="ts">
const title = "TextBox demo";

import { onMounted, ref } from "vue";

export interface Props {
}
</script>

<script setup lang="ts">
import TextBox from "../../src/ui/layout/TextBox.vue";

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
    <div class="textbox-demo" ref="rootElement">
        <TextBox> A cat and a dog they are playing.. </TextBox>
    </div>
</template>

<style scoped lang="scss">
.textbox-demo {
    padding: 0;

    ::v-deep(>.textbox) {
        width: 300px;
        height: 100px;
        resize: both;
        border: solid 1px red;
        font-size: 24px;
    }
}
</style> }
