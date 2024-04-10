<script lang="ts">
import { computed, onMounted, ref } from "vue";
import { calculateLineHeight, getLineCount, reWrap } from "../../dom/text";
import CssUnitValue from "../../dom/CssUnitValue";

export interface Props {
    preserveLineWrapping?: string | boolean
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    preserveLineWrapping: true,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

// app state
const viewVersion = ref(0);

const initialFontSize = ref<string>('');
const initialLines = ref<number>(1);

const lineHeight = computed(() => {
    viewVersion.value;
    let textDiv = textElement.value;
    return textDiv == null ? -1 : calculateLineHeight(textDiv)
});
const lineCount = computed(() => {
    viewVersion.value;
    let textDiv = textElement.value;
    return textDiv == null ? -1 : getLineCount(textDiv);
});
const wrappedLines = ref<string[]>([]);

const scaleToMoreWrap = ref<number | undefined>();
const scaleToLessWrap = ref<number | undefined>();

const fontScale = ref(1);
const fontScaleFmt = computed(() => {
    let scale = fontScale.value;
    return Math.floor(scale * 10000) / 100 + "%";
});
const fontScalePercent = computed(() => fontScale.value * 100 + "%");

// DOM references
const rootElement = ref<HTMLElement>();
const textElement = ref<HTMLElement>();

const clientHeight = computed(() => {
    viewVersion.value;
    let textDiv = textElement.value;
    return textDiv == null ? -1 : textDiv.clientHeight;
});
const scrollHeight = computed(() => {
    viewVersion.value;
    let textDiv = textElement.value;
    return textDiv == null ? -1 : textDiv.scrollHeight;
});

// methods
defineExpose({ update });

// const
function update() {
    viewVersion.value++;

    let rootDiv = rootElement.value!;
    let textDiv = textElement.value!;

    if (props.preserveLineWrapping)
        wrappedLines.value = reWrap(textDiv);

    let currentLines = getLineCount(textDiv);
    let targetLines = initialLines.value;

    scaleToMoreWrap.value = findScaleForMoreWrap(textDiv, targetLines);
    scaleToLessWrap.value = findScaleForLessWrap(textDiv, targetLines);

    let fitScale = findScaleToFit(textDiv, scaleToLessWrap.value, scaleToMoreWrap.value, //
        targetLines);

    let fontSizeStr = initialFontSize.value;
    if (fontSizeStr.length == 0)
        fontSizeStr = '100%';

    let fontSize = CssUnitValue.parse(fontSizeStr);
    let fitFontSize = fontSize.mul(fitScale);

    fontScale.value = fitScale;
    textDiv.style.fontSize = fitFontSize.toString();
}

function findScaleForMoreWrap(el: HTMLElement, targetLines?: number) {
    return findScaleForWrapChange(el, 'more', targetLines);
}

function findScaleForLessWrap(el: HTMLElement, targetLines?: number) {
    return findScaleForWrapChange(el, 'less', targetLines);
}

function findScaleForWrapChange(el: HTMLElement, dir: 'more' | 'less', targetLines?: number) {
    // let origFontSize = el.style.fontSize;
    if (targetLines == null) {
        el.style.fontSize = initialFontSize.value;
        targetLines ||= getLineCount(el);
    }

    let origFontSize = el.style.fontSize;
    let parent = el.parentElement!;
    let origOverflow = parent.style.overflow;
    parent.style.overflow = 'scroll';

    let baseFontSizeStr = initialFontSize.value;
    if (baseFontSizeStr == '')
        baseFontSizeStr = '100%';
    let baseFontSize = CssUnitValue.parse(baseFontSizeStr);

    let k = 1.25;
    let limit = 20.0;
    if (dir == 'less') {
        k = 1 / k;
        limit = 1 / limit;
    }

    try {
        let scale = 1;
        while (true) {
            scale *= k;
            if (dir == 'more') {
                if (scale > limit) break;
            } else {
                if (scale < limit) break;
            }

            let scaledSize = baseFontSize.mul(scale);
            el.style.fontSize = scaledSize.toString();
            let lines = getLineCount(el);
            if (lines != targetLines)
                return scale;
        }
        return undefined;
    } finally {
        el.style.fontSize = origFontSize;
        parent.style.overflow = origOverflow;
    }
}

function findScaleToFit(el: HTMLElement, scaleMin?: number, scaleMax?: number, targetLines?: number) {
    if (targetLines == null) {
        el.style.fontSize = initialFontSize.value;
        targetLines ||= getLineCount(el);
    }

    let origFontSize = el.style.fontSize;
    let parent = el.parentElement!;
    let origOverflow = parent.style.overflow;
    parent.style.overflow = 'scroll';

    let baseFontSizeStr = initialFontSize.value;
    if (baseFontSizeStr == '')
        baseFontSizeStr = '100%';
    let baseFontSize = CssUnitValue.parse(baseFontSizeStr);

    try {
        let min = scaleMin || 0.1;
        let max = scaleMax || 10;
        while (min < max && max - min > .001) {
            let ptr = (min + max) / 2;

            let scaledSize = baseFontSize.mul(ptr);
            el.style.fontSize = scaledSize.toString();

            let lines = getLineCount(el);
            if (lines <= targetLines)
                min = ptr;
            else
                max = ptr;
        }
        return (min + max) / 2;
    } finally {
        el.style.fontSize = origFontSize;
        parent.style.overflow = origOverflow;
    }
}

onMounted(() => {
    let parentEl = rootElement.value!;
    let textEl = textElement.value!;

    initialFontSize.value = textEl.style.fontSize;
    initialLines.value = getLineCount(textEl);

    let observer = new ResizeObserver((mutations) => { update(); });
    observer.observe(parentEl);
    update();
});
</script>

<template>
    <div class="textbox" ref="rootElement" contenteditable="true">
        <div class="text" ref="textElement" v-bind="$attrs">
            <slot></slot>
        </div>
    </div>
    <div class="debug">
        <div class="props">
            <span> lineHeight: {{ lineHeight }} </span>
            <span> lineCount: {{ lineCount }} </span>
            <span> initialLines: {{ initialLines }} </span>
            <span> clientHeight: {{ clientHeight }} </span>
            <span> scrollHeight: {{ scrollHeight }} </span>
            <span> scaleToLessWrap: {{ scaleToLessWrap }} </span>
            <span> scaleToMoreWrap: {{ scaleToMoreWrap }} </span>
            <span> fontScale: {{ fontScaleFmt }} </span>
        </div>
        <ul>
            <li v-for="(line, i) in wrappedLines" :key="i">{{ line }} </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
.textbox {
    overflow: hidden;
}

.text {
    background-color: #eee;
}

.debug {
    //display: none;
    border-top: solid 1px gray;

    .props {
        :not(:first-child) {
            &::before {
                content: ",";
            }
        }
    }
}
</style>
