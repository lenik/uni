<script lang="ts">
import { computed, onMounted, ref } from "vue";
import { } from "./types";

export interface Props {
    srcs: string[]
    columns?: number
    randomCount?: number
    blankRatio?: number
    blankImage?: string
    cellWidth?: string
    cellHeight?: string
}

export function split(array, columns = 4) {
    let rows: any[] = [];
    let n = array.length;
    let row: any[] = [];
    for (let i = 0; i < n; i++) {
        row.push(array[i]);
        if (row.length % columns == 0) {
            rows.push(row);
            row = [];
        }
    }
    if (row.length) rows.push(row);
    return rows;
}

export function randomPick(array: any[], count: number, blankRatio = 0, blankImage?: string) {
    let v: any[] = [];
    for (let i = 0; i < count; i++) {
        let index = Math.floor(Math.random() * array.length);
        if (Math.random() < blankRatio)
            v.push(blankImage);
        else
            v.push(array[index]);
    }
    return v;
}

</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    columns: 12,
    cellWidth: '6em',
    cellHeight: '6em',
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const shuffle = computed(() => props.randomCount == null ? props.srcs :
    randomPick(props.srcs, props.randomCount, props.blankRatio, props.blankImage));
const rows = computed(() => split(shuffle.value, props.columns));

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="image-grid" ref="rootElement">
        <ul class="image-rows">
            <li v-for="(row, i) in rows" :key="i">
                <ol class="image-row">
                    <li class="cell" v-for="(image, j) in row" :key="j">
                        <img :src="image" v-if="image != null" />
                    </li>
                </ol>
            </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}


.image-grid {

    ul,
    ol {
        list-style: none;
        margin: 0;
        padding: 0;

        li {
            display: block;
        }
    }

    .image-rows {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .image-row {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .cell {
        width: v-bind(cellWidth);
        height: v-bind(cellHeight);
        border: solid 1px white;
        margin: .5px;
    }

    img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        // filter: grayscale(50%) saturate(30%) 

        &:-moz-broken {
            display: none;
        }
    }

}
</style>
