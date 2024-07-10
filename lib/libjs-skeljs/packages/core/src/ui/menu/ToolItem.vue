<script setup lang="ts">

import $ from 'jquery';

import { computed, onMounted, Ref, ref } from "vue";
import type { Command } from '../types';
import { bool } from '../types';

import Icon from '../Icon.vue';
import { truncate } from "fs/promises";

const modelValue = defineModel();

interface Props {
    cmd?: Command
    label?: string
    icon?: string

    tagName?: string
    target?: any

    showLabel?: boolean | string

    direction?: 'h' | 'v'
    mode?: 'basic' | 'menu' | 'options'
}

const props = withDefaults(defineProps<Props>(), {
    cmd: { name: 'unspecified' },
    tagName: 'div',
    showLabel: true,
    direction: 'h',
    mode: 'options',
});

const withLabel = computed(() => bool(props.showLabel));

const flexDir = computed(() => props.direction == 'v' ? 'column' : 'row');

const button = computed(() => props.cmd.type == null || props.cmd.type == 'button');
const enabled = computed(() => props.cmd.enabled != false);
const disabled = computed(() => props.cmd.enabled == false);

const toggler = computed(() => props.cmd.type == 'toggle');

const checked = computed(() => {
    switch (props.mode) {
        case 'menu':
            return menuVisible.value;
        case 'options':
            return optionsVisible.value;
        default:
            return (props.cmd.type == 'toggle' && props.cmd.checked)
    }
});

const unchecked = computed(() => {
    switch (props.mode) {
        case 'menu':
            return menuVisible.value;
        case 'options':
            return optionsVisible.value;
        default:
            return (props.cmd.type == 'toggle' && !props.cmd.checked)
    }
});

const menuVisible = ref<boolean>(false);
const optionsVisible = ref<boolean>(false);

interface Emits {
    (e: 'created', event: Event): void
    (e: 'checked', event: Event): void
    (e: 'unchecked', event: Event): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();
const rectDiv = ref<HTMLElement | null>();
const menuDiv = ref<HTMLElement | null>();
const optionsDiv = ref<HTMLElement | null>();

onMounted(() => {
});

function togglePopup(state: Ref<boolean>, div: HTMLElement, belowOf: HTMLElement) {
    if (state.value = !state.value) {
        popup(div, belowOf);
    } else {
        $(div).hide();
    }
}

function popup(div: HTMLElement, belowOf: HTMLElement) {
    let pos = $(belowOf).position();
    let h = $(belowOf).height()!;
    let popX = pos.left;
    let popY = pos.top + h;
    $(div).css("left", popX + "px");
    $(div).css("top", popY + "px");
    $(div).show();
}

function onclick(event: Event) {
    let cmd = props.cmd;
    switch (props.mode) {
        case 'menu':
            togglePopup(menuVisible, menuDiv.value!, rectDiv.value!);
            break;

        default:
    }

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

function onclickOptionsHandler(event: Event) {
    togglePopup(optionsVisible, optionsDiv.value!, rectDiv.value!);
}

defineExpose({
});
</script>

<template>
    <component :is="tagName" ref="rootElement" class="toolitem">
        <component :is="cmd.href != null ? 'a' : 'div'" ref="rectDiv" class="rect"
            :class="{ withLabel, button, toggler, enabled, disabled, checked, unchecked }" :name="cmd.name">
            <div class="join" :href="cmd.href" :title="cmd.tooltip" @click="(e) => onclick(e)">
                <div class="hover"></div>
                <slot name="image">
                    <Icon :name="icon || cmd.icon" v-if="icon != null || cmd.icon != null" />
                </slot>
                <span class="sep" v-if="withLabel"></span>
                <span class="label" v-if="withLabel">{{ label || cmd.label || cmd.name }}</span>
                <Icon class="menu-handler" name="fa-caret-down" v-if="mode == 'menu'" />
            </div>
            <Icon class="options-handler" name="fa-caret-down" v-if="mode == 'options'"
                @click="(e) => onclickOptionsHandler(e)" />
        </component>
        <div class="menu" ref="menuDiv" v-if="mode == 'menu'">
            <div>Menu header</div>
            <div>DDD</div>
            <div>DDD</div>
            <div>DDD</div>
            <slot name="menu"> Menu 111 </slot>
        </div>
        <div class="options" ref="optionsDiv" v-if="mode == 'options'">
            <div>Options header</div>
            <slot name="options"> Options 111 </slot>
        </div>
    </component>
</template>

<style scoped lang="scss">
.toolitem {
    padding: inherit;
    display: inline-block;
}

.button.disabled {
    color: gray;
}

.rect {
    display: flex;
    flex-direction: v-bind(flexDir);
    align-items: center;
}

.join {
    display: flex;
    flex-direction: v-bind(flexDir);
    align-items: center;
    cursor: default;
    user-select: none;
    position: relative;
    // border: solid 1px red;

    .hover {
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        right: 0;
        margin: 0; // 2px;
        border-radius: 2px;
    }

    .icon {
        font-size: 150%;
        padding: .1em;
    }

    &.unchecked .icon {
        opacity: 0.5;
    }

    .sep {
        height: .1em;
    }

    .label {
        font-size: 75%;
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

    border-color: hsl(190, 45%, 30%);

    &:hover .hover {
        background-color: hsl(200, 70%, 85%); // !important;
        opacity: 40%;
    }
}

.toggler {
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

.menu-handler,
.options-handler {
    padding: 0 2px;
    font-size: 80% !important;

    &:hover {
        background-color: hsl(200, 70%, 85%); // !important;
    }
}

.menu-handler {
    color: black;
}

.options-handler {
    align-self: stretch;
    align-content: center;
}

.menu,
.options {
    border: solid 1px gray;
    display: none;
    position: absolute;
    background-color: hsl(200, 15%, 95%);
    box-shadow: 2px 2px 5px #888a;
    user-select: none;
    padding: .2em  .5em;
}

.menu {}

.options {}

.description {
    display: none;
}
</style>
