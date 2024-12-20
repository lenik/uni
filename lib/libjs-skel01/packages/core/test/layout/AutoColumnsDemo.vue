<script lang="ts">
const title = "Auto columns demo";

import { computed, onMounted, ref } from "vue";
import { loremIpsum } from "lorem-ipsum";

export interface Props {
}
</script>

<script setup lang="ts">
import AutoColumns from "../../src/ui/layout/AutoColumns.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

// app state
const npars = ref(20);
const pars = computed(() =>
    Array(npars.value).fill('').map(a =>
        loremIpsum({
            count: npars.value,
            units: 'sentences',
        })
    ));

const widthSlider = ref(300);
const widthUnit = ref('px');
const widthStep = computed(() => widthUnit.value == 'px' ? 10 : 1);
const columnWidth = computed(() => widthSlider.value + widthUnit.value);

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
        <div class="box"> &nbsp; </div>
        <div class="options">
            <span> column-width: </span>
            <input type="number" size="4" :step="widthStep" v-model="widthSlider">
            <span> Unit: </span>
            <select v-model="widthUnit">
                <option value="px">px</option>
                <option value="em">em</option>
            </select>
        </div>
        <hr>
        <AutoColumns :columnWidth="columnWidth" columnGap="20px">
            <p v-for="(p, i) in pars" :key="i"> {{ p }} </p>
        </AutoColumns>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    font-family: monospace;
    position: relative;
}

input {
    width: 4em;
    text-align: right;
}

.box {
    position: absolute;
    top: 0;
    left: 0;
    width: v-bind(columnWidth);
    height: 3em;
    background-color: pink;
    opacity: 0.5;
    border: solid 1px black;
    z-index: -1;
}

::v-deep(.columns) {
    column-rule: 2px dashed #ccc;
}


p {
    border: solid 1px #eee;
}
</style>
