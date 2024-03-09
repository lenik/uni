<script lang="ts">
import { onMounted, ref } from "vue";

export interface Props {
}
</script>

<script setup lang="ts">
import JsonEditor from '../src/ui/input/JsonEditor.vue';

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

const json = ref({
    msg: 'demo of json11editor'
});

function onJsonChange(value) {
    console.log('value:', value)
}
function onTextChange(value: string) {
    console.log('text:', value)
}


</script>

<template>
    <div class="component-root" ref="rootElement">
        <JsonEditor v-model="json" @changeJson="onJsonChange" @changeText="onTextChange"></JsonEditor>
        <pre v-text="json"></pre>
    </div>
</template>

<style lang="scss">
.main {
    display: flex;
    flex-direction: column;
}
</style>

<style scoped lang="scss">
.component-root {
    flex: 1;
    display: flex;
    flex-direction: row;

    >* {
        flex: 1;
        height: 100%;
    }

    pre {
        border: solid 1px gray;
    }
}
</style>
