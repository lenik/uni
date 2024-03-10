<script setup lang="ts">

// import 'jquery-ui/themes/base/base.css';
// // import 'jquery-ui/themes/base/selectable.css';

import $ from "jquery";

import { computed, onMounted, ref } from "vue";

import Icon from '../Icon.vue';
import { makeMovable } from "../../dom/movable";

const model = defineModel();

interface Props {
    title?: string
    detached?: boolean
    transparent?: boolean

    detachedWidth?: string
    detachedHeight?: string
}

const props = withDefaults(defineProps<Props>(), {
    title: 'Detachable',
    detached: false,
    transparent: false,
});

const emit = defineEmits<{
    error: [message: string]
}>();

const _detached = ref(props.detached);
const _attached = computed(() => !_detached.value);

const transparentMode = ref<boolean>(props.transparent);
const _transparent = computed(() => {
    if (_attached.value) return false;
    return transparentMode.value;
});

defineOptions({
    inheritAttrs: false
})

const rootElement = ref<HTMLElement>();
const titleElement = ref();

onMounted(() => {
    makeMovable(rootElement.value!, titleElement.value);
});

function findContainer(elm: HTMLElement): HTMLElement | null {
    let node = elm.parentElement;
    while (node != null) {
        let styleMap = node.computedStyleMap();
        let overflow = styleMap.get('overflow');
        if (overflow != null) {
            switch (overflow.toString()) {
                case 'hidden':
                    return node;
                // case 'scroll':
                // case 'auto':
            }
        }
        node = node.parentElement;
    }
    return null;
}

function enlargeWidth(scale: number, add = 0) {
    let div = rootElement.value!;
    // let styleMap = div.computedStyleMap();
    // let styleWidth: CSSNumericValue = styleMap.get("width") as CSSNumericValue;
    // let newWidth = styleWidth.mul(scale).toString();
    let width = div.offsetWidth;
    let height = div.offsetHeight;
    let newWidth = width * scale + add;
    let newHeight;

    let container = findContainer(div);
    // || document.body;
    if (container != null) {
        let maxWidth = container.offsetWidth - 30;
        let maxHeight = container.offsetHeight - 30;

        if (newWidth > maxWidth)
            newWidth = maxWidth;

        if (height > maxHeight)
            newHeight = maxHeight;
    }

    if (newWidth != null)
        div.style.width = newWidth + 'px';
    if (newHeight != null)
        div.style.height = newHeight + 'px';
}

function addColumn(n: number) {
    let div = rootElement.value!;
    let frameWidth = 40;
    let columnWidth = 400;
    enlargeWidth(1, columnWidth);
    let columns = div.getAttribute('columns');
    let nCol = 1;
    if (columns != null)
        nCol = parseInt(columns);
    let nNewCol = nCol + n;
    div.setAttribute('columns', String(nNewCol));
}
</script>

<template>
    <div ref="rootElement" class="detachable"
        :class="{ attached: _attached, detached: _detached, transparent: _transparent }" v-bind="$attrs">
        <div class="header">
            <div ref="titleElement" class="title">{{ title }}</div>
            <div class="toggler" @click="transparentMode = !transparentMode" v-if="_detached">
                <Icon :name="transparentMode ? 'far-eye' : 'fas-low-vision'" />
            </div>
            <div class="toggler" @click="addColumn(1)" title="Enlarge width by 2x.">
                <Icon name="far-arrows-alt-h" />
            </div>
            <div class="toggler" @click="_detached = !_detached" :title="_detached ? 'Attach' : 'Detach'">
                <Icon :name="_detached ? 'far-thumbtack' : 'far-window-restore'" />
            </div>
        </div>
        <div class="content">
            <slot>
                <div class="demo">
                    <h3>Features</h3>
                    <ul>
                        <li>Attach into the side pane.</li>
                        <li>Detachable as a window which can be moved and resized.</li>
                        <li>Transparent control: switch between transparent mode and opaque mode.</li>
                    </ul>
                </div>
            </slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.detachable {
    padding: 0;
    min-width: 10em;
    display: flex;
    flex-direction: column;

    --frame-color: hsl(190, 30%, 65%);
    --frame-width: 2px;

    .header {
        display: flex;
        flex-direction: row;
        align-items: stretch;
        cursor: pointer;
        user-select: none;

        // border-bottom: solid 1px var(--border-color);
        background-color: var(--frame-color);
        color: white;

        .title {
            flex: 1;
            padding: 2px 4px;
        }

        .toggler:last-child {
            padding-right: 4px;
        }

        .toggler {
            margin-left: 4px;
            padding-left: 4px;

            display: flex;
            flex-direction: row;
            align-items: center;

            border-left: solid 1px gray;
            color: black;
        }
    }

    &.attached {
        position: static;
        width: auto !important;
        overflow: hidden;

        .content {
            overflow: auto;
        }
    }

    &.detached {
        position: absolute;
        top: 1em;
        right: 1em;
        min-width: 10em;
        min-height: 10em;
        width: v-bind(detachedWidth);
        height: v-bind(detachedHeight);
        resize: auto;
        overflow: auto;

        border-style: solid;
        border-width: var(--frame-width);
        border-color: var(--frame-color);
        border-radius: .3em;

        .content {
            flex: 1;
            overflow: auto;
            background-color: hsl(0, 0%, 95%);
            // aspect-ratio: 3;
        }
    }

    &.transparent .content {
        opacity: 50%;
        background-color: hsl(0, 0%, 90%);
    }

}

.detachable.detached {
    &[columns='2']>.content {
        columns: 2;
    }

    &[columns='3']>.content {
        columns: 3;
    }

    &[columns='4']>.content {
        columns: 4;
    }
}

.demo {
    padding: .5em;
}

.attached .demo {
    max-width: 15em;
}

.detached .demo {
    width: 100%;
    box-sizing: border-box;
}
</style>
