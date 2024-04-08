<script lang="ts">
import { computed, onMounted, ref } from "vue";
import CssUnitValue from "../../dom/CssUnitValue";

export interface Props {
    columnWidth: string | number,
    columnGap?: string | number,
    maxColumns?: number
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    maxColumns: 10,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts
const realColumnGap = computed(() => props.columnGap || '20px');

// app state
const styleHtml = computed(() => {
    let context = {
        element: rootElement.value
    };
    let columnWidth = CssUnitValue.parse(props.columnWidth);
    let columnGap = CssUnitValue.parse(realColumnGap.value);

    let css = '';
    for (let n = 2; n <= props.maxColumns; n++) {
        let minWidth = columnWidth.mul(n).add(columnGap.mul(n - 1));
        let maxWidth = minWidth.add(columnWidth).add(columnGap);

        // let condition = '(min-width: ' + minWidth + ")";
        // if (n != props.maxColumns)
        //     condition += ' and (max-width: ' + maxWidth + ")";
        let condition = '(width >= ' + minWidth + ")";
        if (n != props.maxColumns)
            condition += ' and (width < ' + maxWidth + ")";

        css += '@container autoColumns ' + condition + ' {\n';
        css += '.auto-columns>.columns { columns: ' + n + '; }\n';
        css += '}\n';
    }
    console.log(css);
    let html = "<style type='text/css'>" + css + "</style>";
    return html;
});

// DOM references
const rootElement = ref<HTMLElement>();
const styleHack = ref<HTMLElement>();

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
    // let div = styleHack.value!;
    // div.innerHTML = "<style type='text/css'>" + css + "</style>";
});
</script>

<template>
    <div class="auto-columns" ref="rootElement">
        <div class="columns">
            <slot></slot>
        </div>
    </div>
    <div class="styleHack" v-html="styleHtml"></div>
</template>

<style scoped lang="css">
.auto-columns {
    /* columns: 2; */
    container: autoColumns / inline-size;

    >.columns {
        column-gap: v-bind(columnGap);
    }
}
</style>