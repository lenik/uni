<script setup lang="ts">

// import 'jquery-ui/themes/base/base.css';
// // import 'jquery-ui/themes/base/selectable.css';

import $ from "jquery";
// await import "jquery-ui/ui/core";
// import "jquery-ui/ui/widgets/resizable";
// // import "jquery-ui-esm/widgets/draggable";

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

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

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

const rootElement = ref();
const titleElement = ref();

onMounted(() => {
    makeMovable(rootElement.value, titleElement.value);
});


</script>

<template>
    <div ref="rootElement" class="detachable"
        :class="{ attached: _attached, detached: _detached, transparent: _transparent }" v-bind="$attrs">
        <div class="header">
            <div ref="titleElement" class="title">{{ title }}</div>
            <div class="toggler" @click="transparentMode = !transparentMode" v-if="_detached">
                <Icon :name="transparentMode ? 'far-eye' : 'fas-low-vision'" />
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
