<script lang="ts">
import { computed, onMounted, ref } from "vue";
import { parseJson, toJson } from "../src/lang/json";

export const title = 'JSON with (circular) references: parser and formatter test.';

export interface Props {
}
</script>

<script setup lang="ts">
import JsonEditor from "../src/ui/input/JsonEditor.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();

const _people: any = {
    Tom: {
        name: 'Tom',
        age: 13,
    },
    Jack: {
        name: 'Jack',
        age: 87
    }
};
_people.Tom.parent = _people.Jack;

const people = ref(_people);
const peopleJson = computed(() => toJson(people.value));
const peopleRestore = computed({
    get() { return parseJson(peopleJson.value); },
    set(val) { }
});

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <JsonEditor v-model="people" />
        <div>
            <p> toJson: </p>
            <textarea v-text="peopleJson" />
        </div>
        <div> Restored:
            <JsonEditor v-model="peopleRestore" />
        </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
    position: absolute;

    top: 0;
    bottom: 0;
    left: 0;
    right: 0;

    display: flex;
    flex-direction: row;

    >* {
        flex: 1;
        position: relative;
        display: flex;
        flex-direction: column;

        &:not(:first-child) {
            margin-top: .5em;
        }
    }

    ::v-deep(.jsoneditor-wrapper) {
        // border: solid 2px red;
        flex: 1;
    }

    textarea {
        width: 100%;
        height: 100%;
    }

}
</style>