<script setup lang="ts">

import $ from 'jquery';

import { computed, onMounted, ref } from "vue";
import { bool, Command } from './types';

import Icon from './Icon.vue';

const modelValue = defineModel();

interface Props {
    cmd: Command
    tagName?: string
    target?: any

    showIcon?: boolean | string
    showLabel?: boolean | string
    showBorder?: boolean | string
}

const props = withDefaults(defineProps<Props>(), {
    tagName: 'div',
    showIcon: true,
    showLabel: true,
    showBorder: true,
});

const withIcon = computed(() => bool(props.showIcon));
const withLabel = computed(() => bool(props.showLabel));
const withBorder = computed(() => bool(props.showBorder));

const button = computed(() => props.cmd.type == null || props.cmd.type == 'button');
const toggle = computed(() => props.cmd.type == 'toggle');
const checked = computed(() => (props.cmd.type == 'toggle' && props.cmd.checked));
const unchecked = computed(() => (props.cmd.type == 'toggle' && !props.cmd.checked));

interface Emits {
    (e: 'created', event: Event): void
    (e: 'checked', event: Event): void
    (e: 'unchecked', event: Event): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();

onMounted(() => {
});

function onclick(event) {
    let cmd = props.cmd;
    switch (cmd.type) {
        case 'toggle':
            props.cmd.checked = !props.cmd.checked;
            if (props.cmd.checked)
                emit('checked', event);
            else
                emit('unchecked', event);
            break;

        case 'button':
        default:
    }

    if (cmd.run != null)
        cmd.run(event, cmd);

    if (cmd.href != null)
        location.href = cmd.href;
}

defineExpose({
});
</script>

<template>
    <component :is="tagName" ref="rootElement" class="cmd-button">
        <div class="btn-with-extras">
            <component :is="cmd.href != null ? 'a' : 'div'"
                :class="{ withIcon, withLabel, withBorder, button, toggle, checked, unchecked }" :name="cmd.name"
                :href="cmd.href" :title="cmd.tooltip" @click="(e) => onclick(e)">

                <div class="hover"></div>

                <Icon :name="cmd.icon" v-if="withIcon && cmd.icon != null" />

                <span class="sep" v-if="withIcon && withLabel"></span>

                <span class="label" v-if="withLabel">{{ cmd.label || cmd.name }}</span>

            </component>

            <Icon name="fa-ques" v-if="cmd.description != null" />
        </div>

        <div class="description" v-if="cmd.description != null">
            {{ cmd.description }}
        </div>
    </component>
</template>

<style scoped lang="scss">
.cmd-button {
    padding: inherit;
}

.button,
.toggle {
    display: flex;
    flex-direction: row;
    align-items: center;
    cursor: pointer;
    user-select: none;
    position: relative;

    &.withLabel {
        margin: .3em;
        padding: .3em .8em;
    }

    &:not(.withLabel) {
        margin: .2em;
        padding: .2em;
    }

    &.withBorder {
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
    }

    .hover {
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        right: 0;
        margin: 0em;
        border-radius: 5px;
    }

    .icon {}

    &.unchecked .icon {
        opacity: 0.5;
    }

    .sep {
        width: .5em;
    }

    .label {
        font-weight: 300;
        white-space: nowrap;
    }

    &.checked .label {
        // font-weight: bold;
    }

    >* {
        // z-index: 1;
    }

}

.button {

    &.withBorder {
        background-color: hsl(190, 45%, 85%);
        border-color: hsl(190, 45%, 30%);

    }

    &:hover .hover {
        background-color: hsl(60, 90%, 85%) !important;
        opacity: 40%;
    }
}

.toggle {
    &.withBorder {

        &.unchecked {
            background-color: hsl(190, 10%, 90%);
            border-color: hsl(190, 45%, 30%);
            color: gray;
        }

        &.checked {
            background-color: hsl(190, 50%, 50%);
            border-color: hsl(190, 45%, 30%);
            color: white;
        }
    }
}

.description {
    display: none;
}
</style>
