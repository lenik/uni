<script lang="ts">
import { onMounted, ref } from "vue";

export interface Props {
}
</script>

<script setup lang="ts">
import AttachmentsEditor from '../../src/ui/input/AttachmentsEditor.vue';

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const a1 = {
    "schema": "person",
    "dir": "person/1000",
    "name": "timg (3).jpeg",
    "sha1": "0228678e9645941c2673f57a6a4c5ac7033ce583",
    "size": 151889,
};

const attachments = ref([]);

// methods

defineExpose({ update });

function update() {
}

function save() {

}

function reload() {

}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <AttachmentsEditor className="Person" id="1000" v-model="attachments" />
        <div>
            <input type="button" value="Save" @click="save()">
            <input type="button" value="Reload" @click="reload()">
        </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}
</style>
