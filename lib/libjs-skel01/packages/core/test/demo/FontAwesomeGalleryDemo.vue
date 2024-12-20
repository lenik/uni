<script lang="ts">
const title = "component FontAwesomeGalleryDemo";

import { onMounted, ref } from "vue";

export interface Props {
}
</script>

<script setup lang="ts">
import Icon from "../../src/ui/Icon.vue";
import FontAwesomeGallery from "../../src/ui/demo/fa-gallery/FontAwesomeGallery.vue";

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
const fontAwesomeGalleryElement = ref<InstanceType<typeof FontAwesomeGallery>>();

// methods
defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <FontAwesomeGallery ref="fontAwesomeGalleryElement">
        </FontAwesomeGallery>
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
