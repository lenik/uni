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

const content = computed(() => {
    let status = props.status;
    let msg = status.message;
    return msg;
});

const haveContent = computed(() => bool(content.value != null));

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
    <component :is="tagName" ref="rootElement" class="status-panel"
        :class="{ withIcon, withLabel, withBorder, haveContent }">
        <Icon :name="status.icon" v-if="withIcon && status.icon != null" />
        <span class="sep" v-if="withIcon && withLabel"></span>
        <span class="label" v-if="withLabel">{{ status.label || status.name }}</span>
        <span class="content">{{ '' + content }}</span>
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

    .icon {
        color: #644;
    }

    .sep {
        width: .5em;
    }

    .label {
        font-weight: 300;
        // font-weight: lighter;
        white-space: nowrap;
    }

    .content {
        margin-left: .5em;
        font-weight: 300;
        color: hsl(190, 30%, 30%);
        white-space: nowrap;
        overflow: hidden;
    }

}
</style>
