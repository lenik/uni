<script lang="ts">
import $ from 'jquery';
import { computed, defineComponent, onMounted, ref } from "vue";

export interface Props {
    random?: boolean
    blankRatio?: number
    blankImage?: string
    cellWidth?: string
    cellHeight?: string
}

export function generate(random: boolean, array: any[], count: number, blankRatio = 0, blankImage?: string) {
    let v: any[] = [];
    for (let i = 0; i < count; i++) {
        let index: number;
        if (random)
            index = Math.floor(Math.random() * array.length);
        else
            index = i % array.length;

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
    random: false,
    cellWidth: '6em',
    cellHeight: '6em',
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const version = ref(0);
const srcs = ref<string[]>([]);

const rowCountAppx = ref(0);
const columnCountAppx = ref(0);

const shuffle = computed(() => {
    version.value == 0;
    let count = computeAppxCount();
    return generate(props.random, srcs.value, count, props.blankRatio, props.blankImage);
});

function computeAppxCount(defaultCount = 3) {
    if (rootElement.value == null) return defaultCount;
    let cells = rootElement.value.getElementsByClassName('cell');
    if (cells.length == 0) return defaultCount;

    let parent: HTMLElement | null = rootElement.value;
    let parentStyle;
    while (parent != null) {
        parentStyle = parent.computedStyleMap();
        if (parentStyle.get("overflow") == 'hidden')
            break;
        parent = parent.parentElement;
    }
    if (parent == null) return defaultCount;

    let refCell = cells.item(0)!;
    let pw = parent.offsetWidth;
    let ph = parent.offsetHeight;
    let cw = refCell.clientHeight;
    let ch = refCell.clientHeight;

    let cellstyle = refCell.computedStyleMap();
    let marginLeft = cellstyle.get("margin-left")! as any;
    let marginRight = cellstyle.get("margin-right")!;
    let marginTop = cellstyle.get("margin-top")! as any;
    let marginBottom = cellstyle.get("margin-bottom")!;
    let marginH = marginLeft.add(marginRight);
    let marginV = marginTop.add(marginBottom);
    cw += marginH.value;
    ch += marginV.value;

    // let cols = Math.floor((pw + cw - 1) / cw);
    // let rows = Math.floor((ph + ch - 1) / ch);
    let cols = Math.floor(pw / cw) + 1;
    let rows = Math.floor(ph / ch) + 1;

    rowCountAppx.value = rows;
    columnCountAppx.value = cols;
    let count = cols * rows;
    return count;
}

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
    let v: string[] = [];
    let srcsDivs = rootElement.value!.getElementsByClassName("srcs");
    if (srcsDivs.length != 0) {
        let srcsDiv = srcsDivs.item(0)!;
        let imgs = srcsDiv.getElementsByTagName('img');
        for (let i = 0; i < imgs.length; i++) {
            let src = imgs[i].getAttribute('src');
            if (src != null)
                v.push(src);
        }
        srcs.value = v;
        srcsDiv.remove();
    }

    window.addEventListener('resize', () => {
        version.value++;
    });
});
</script>

<template>
    <div class="image-grid" ref="rootElement">
        <div class="srcs">
            <slot></slot>
        </div>
        <ul class="series">
            <li class="cell" v-for="(image, j) in shuffle" :key="j">
                <img :src="image" v-if="image != null" />
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

    .series {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
        margin-right: calc(-1* v-bind(cellWidth));
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
