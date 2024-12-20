<script setup lang="ts">

import $ from 'jquery';

import { onMounted, ref } from "vue";
import { resolveChild } from "../dom/create";
import { Command } from './types';

import Icon from './Icon.vue';

const modelValue = defineModel();

interface Props {
    center?: string
}

const props = withDefaults(defineProps<Props>(), {
    center: 'window',
});

interface Emits {
    (e: 'closed', event: Event): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();

onMounted(() => {
    let parent = resolveChild(document.body, '.sys-modals');
    parent.appendChild(rootElement.value!);
});

function open() {
    $(rootElement.value!).show();
}

defineExpose({
    open
});
</script>

<template>
    <div ref="rootElement" class="component-root modal" id="waitbox">
        <div class="flex-row">
            <div class="content">
                <i class="fa fa-circle-o-notch fa-spin"></i>
                <span class="text">Wait...</span>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {}

.modal {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    cursor: wait;
    background: rgba(128, 128, 128, .6);
    animation: bgloop 1.3s infinite alternate ease-in-out;

    @at-root {
        @keyframes bgloop {
            0% {
                background: hsla(60, 30%, 30%, .3);
            }

            100% {
                background: hsla(60, 30%, 30%, .8);
            }
        }
    }

    .flex-row {
        align-items: center;
        height: 100%;
    }

    .content {
        width: 100%;
        text-align: center;
        font-size: 5em;
        font-family: Sans;
        font-weight: bold;
        color: #356;
    }
}
</style>
