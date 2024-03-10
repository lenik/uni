<script lang="ts">
import { computed, onMounted, ref } from "vue";

export const title = 'JSON Editor demo';

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

const jsonText = computed({
    get(): string {
        return JSON.stringify(json.value);
    },
    set(val: string) {
        let parsed = JSON.parse(val);
        json.value = parsed;
    }
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
        <textarea v-model="jsonText"></textarea>
        <pre v-text="json" />
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
