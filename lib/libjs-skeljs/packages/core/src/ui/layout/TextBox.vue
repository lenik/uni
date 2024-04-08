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

    let initialLines = getLineCount(textDiv);

    scaleToMoreWrap.value = findScaleForMoreWrap(textDiv, initialLines);
    scaleToLessWrap.value = findScaleForLessWrap(textDiv, initialLines);

    let fitScale = findScaleToFit(textDiv, scaleToLessWrap.value, scaleToMoreWrap.value, initialLines);

    let fontSizeStr = initialFontSize.value;
    if (fontSizeStr.length == 0)
        fontSizeStr = '100%';

    let fontSize = CssUnitValue.parse(fontSizeStr);
    let fitFontSize = fontSize.mul(fitScale);

    fontScale.value = fitScale;
    textDiv.style.fontSize = fitFontSize.toString();
}

function findScaleForMoreWrap(el: HTMLElement, initialLines?: number) {
    return findScaleForWrapChange(el, 'more', initialLines);
}

function findScaleForLessWrap(el: HTMLElement, initialLines?: number) {
    return findScaleForWrapChange(el, 'less', initialLines);
}

function findScaleForWrapChange(el: HTMLElement, dir: 'more' | 'less', initialLines?: number) {
    // let origFontSize = el.style.fontSize;
    if (initialLines == null) {
        el.style.fontSize = initialFontSize.value;
        initialLines ||= getLineCount(el);
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
            if (lines != initialLines)
                return scale;
        }
        return undefined;
    } finally {
        el.style.fontSize = origFontSize;
        parent.style.overflow = origOverflow;
    }
}

function findScaleToFit(el: HTMLElement, scaleMin?: number, scaleMax?: number, initialLines?: number) {
    let origOverflow = rootElement.value!.style.overflow;
    rootElement.value!.style.overflow = 'scroll';

    let origFontSize = el.style.fontSize;
    try {
        let min = scaleMin || 1;
        let max = scaleMax || 1;
        while (min < max && max - min > .001) {
            let ptr = (min + max) / 2;
            // fontScale.value = ptr;
            el.style.fontSize = ptr * 100 + "%";
            let lines = getLineCount(el);
            if (lines == initialLines)
                min = ptr;
            else {
                max = ptr;
            }
        }
        return (min + max) / 2;
    } finally {
        el.style.fontSize = origFontSize;
        rootElement.value!.style.overflow = origOverflow;
    }
}

onMounted(() => {
    let parentEl = rootElement.value!;
    let textEl = textElement.value!;

    initialFontSize.value = textEl.style.fontSize;

    let observer = new ResizeObserver((mutations) => { update(); });
    observer.observe(parentEl);
    update();
});
</script>

<template>
    <div class="textbox" ref="rootElement" contenteditable="true">
        <div class="text" :class="{ scaled }" ref="textElement" v-bind="$attrs">
            <slot></slot>
        </div>
    </div>
    <div class="debug">
        <div class="props">
            <span> lineHeight: {{ lineHeight }} </span>
            <span> lineCount: {{ lineCount }} </span>
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
