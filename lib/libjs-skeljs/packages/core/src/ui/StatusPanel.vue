<script setup lang="ts">

import $ from 'jquery';

import { computed, onMounted, ref } from "vue";
import { bool, Status } from './types';

import Icon from './Icon.vue';

const model = defineModel();

interface Props {
    status: Status
    tagName?: string
    showIcon?: boolean | string
    showLabel?: boolean | string
    showBorder?: boolean | string
}

const props = withDefaults(defineProps<Props>(), {
    tagName: 'div',
    showIcon: true,
    showLabel: true,
    showBorder: false,
});

const withIcon = computed(() => bool(props.showIcon));
const withLabel = computed(() => bool(props.showLabel));
const withBorder = computed(() => bool(props.showBorder));

interface Emits {
    (e: 'created', event: Event): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();

onMounted(() => {
});

defineExpose({
});
</script>

<template>
    <component :is="tagName" ref="rootElement" class="status-panel" :class="{ withIcon, withLabel, withBorder }">

        <Icon :name="status.icon" v-if="withIcon && status.icon != null" />

        <span class="sep" v-if="withIcon && withLabel"></span>

        <span class="label" v-if="withLabel">{{ status.label || status.name }}</span>

    </component>
</template>

<style scoped lang="scss">
.status-panel {
    display: flex;
    flex-direction: row;
    align-items: center;
    box-sizing: border-box;
    font-size: 85%;
    cursor: pointer;
    user-select: none;
    margin: 0;

    &.withLabel {
        padding: .2em .5em;
    }

    &:not(.withLabel) {
        padding: .2em;
    }

    &.withBorder {
        background-color: #f8f8f8;
        border-radius: 1px;
        border-style: solid;
        border-width: 1px;
        border-color: #eee;

        &:hover {
            background-color: hsl(190, 45%, 95%);
        }

    }

    .icon {}

    .sep {
        width: .5em;
    }

    .label {
        font-weight: 300;
        white-space: nowrap;
    }

}
</style>
