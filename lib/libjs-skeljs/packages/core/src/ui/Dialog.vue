<script lang="ts">
import $ from 'jquery';

import { computed, onMounted, ref } from "vue";
import { resolveChild } from "../dom/create";
import type { Command } from './types';
import { bool, getDialogCmds } from './types';
import type { AsyncDialogSelectCallback, DialogSelectCallback } from './types';
import { makeMovable } from '../dom/movable';

import Icon from './Icon.vue';
import CmdButtons from './CmdButtons.vue';

export interface Props {
    group?: string
    modal?: boolean | string

    icon?: string
    title?: string
    tabindex?: number

    closable?: boolean | string
    maximizable?: boolean | string
    minimizable?: boolean | string
    resizable?: boolean

    commands?: Command[]
    cmds?: any

    width?: string | number
    height?: string | number
    center?: string

    autoOpen?: boolean | string
    autoFocus?: boolean
    inputs?: string

    unique?: boolean
}

class ModalFrame {
    zIndexBackup?: string

    static modalStack: ModalFrame[] = [];
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    modal: false,
    tabindex: 0,
    closable: true,
    maximizable: false,
    minimizable: false,
    cmds: { close: true },
    center: 'window',
    autoOpen: false,
    autoFocus: true,
    inputs: "input, select, textarea, [tabindex]",
    unique: false,
});

const emit = defineEmits<{
    create: [event: Event]
    close: [event: Event]
    select: [value: any, event: Event]
}>();

// property shortcuts

const blink = computed(() => props.modal == 'blink');
const isModal = computed(() => bool(props.modal) || props.modal == 'blink');
const isClosable = computed(() => bool(props.closable));
const isMaximizable = computed(() => bool(props.maximizable));
const isMinimizable = computed(() => bool(props.minimizable));
const isAutoOpen = computed(() => bool(props.autoOpen));

const initDisplay = computed(() => isAutoOpen.value ? 'block' : 'none');

const buttons = computed(() => {
    let all: Command[] = [];
    if (props.cmds != null) {
        let dialogCmds = getDialogCmds(props.cmds);
        all.push(...dialogCmds);
    }
    if (props.commands != null)
        all.push(...props.commands);
    return all;
});

const hasTitle = computed(() => props.title != null && props.title.length > 0);
const hasButton = computed(() => buttons.value.length > 0);
const _resizable = computed(() => hasTitle.value || props.resizable);

const containerDiv = ref<HTMLElement>();
const dialogDiv = ref<HTMLElement>();
const titleDiv = ref<HTMLElement>();

defineExpose({
    open, rootElement: containerDiv
});

class LocalFrame {
    selectCallback?: DialogSelectCallback | AsyncDialogSelectCallback
}
var localStack: LocalFrame[] = [];
var modalStack: ModalFrame[] = ModalFrame.modalStack;

function open(selectCallback?: DialogSelectCallback | AsyncDialogSelectCallback) {
    let frame = new LocalFrame();
    frame.selectCallback = selectCallback;
    localStack.push(frame);

    let _containerDiv = containerDiv.value!;
    let _dialogDiv = dialogDiv.value!;

    if (isModal.value) {
        let modalFrame = new ModalFrame();

        // let zSrc = _dialogDiv;
        let zSrc = _containerDiv;
        modalFrame.zIndexBackup = zSrc.style.zIndex;
        zSrc.style.zIndex = '' + modalStack.length;

        modalStack.push(modalFrame);
    }

    $(_containerDiv).show();

    _dialogDiv.focus();

    if (props.autoFocus) {
        let inputs = $(props.inputs, dialogDiv.value!);
        if (inputs.length)
            inputs[0].focus();
    }
}

function _destroy() {
    let frame = localStack.pop();
    let _containerDiv = containerDiv.value!;
    let _dialogDiv = dialogDiv.value!;

    if (isModal.value) {
        let modalFrame = modalStack.pop()!;
        if (modalFrame.zIndexBackup != null) {
            // let zSrc = _dialogDiv;
            let zSrc = _containerDiv;
            zSrc.style.zIndex = '' + modalFrame.zIndexBackup;
        }
    }

    $(_containerDiv).hide();

    return frame;
}

function close(e: Event) {
    if (localStack.length) {
        emit('close', e);
        _destroy();
    }
}

async function select(event: Event) {
    if (localStack.length) {
        let frame = localStack[localStack.length - 1];

        if (frame.selectCallback != null) {
            // slowly((end) => {
            let success = frame.selectCallback(model.value, event);
            if (typeof success == 'object')
                if (typeof success.then == 'function') {
                    success = await success;
                }
            if (!success)
                return false;
        }

        emit('select', model.value, event);
        _destroy();
    }
}

function run(button: Command, event: Event) {
    switch (button.action) {
        case 'close':
            close(event);
            break;

        case 'select':
            select(event);
            break;

        case 'maximize':
        default:
            break;
    }

    if (button.run != null)
        button.run(event);
}

onMounted(() => {
    let dialogs = resolveChild(document.body, '.dialogs');
    if (props.group != null) {
        let groupId = '#dialog-group-' + props.group;
        dialogs = resolveChild(dialogs, groupId, true);
    }

    let _containerDiv = containerDiv.value!;
    _containerDiv.remove();

    if (props.unique) {
        if (_containerDiv.id != null)
            if (document.getElementById(_containerDiv.id) != null)
                // duplicated, just skip it.
                return;

        let dialogName = _containerDiv.getAttribute("name");
        if (dialogName != null) {
            let dups = document.getElementsByName(dialogName);
            if (dups.length) // duplicated by name
                return;
        }
    }

    dialogs.appendChild(_containerDiv);

    makeMovable(dialogDiv.value!, titleDiv.value!);

    dialogDiv.value!.addEventListener('keydown', (event: KeyboardEvent) => {
        switch (event.key) {
            case 'Escape':
                close(event);
                break;

            case 'Enter':
                if (props.autoFocus) {
                    // find next siblings after e.currentTarget...
                    let allInputs = $(props.inputs, dialogDiv.value);
                    let i = allInputs.index((event as any).target);
                    let next = i + 1;
                    if (next < allInputs.length) {
                        allInputs[next].focus();
                    } else {
                        select(event);
                    }
                }
                break;
        }
    }, true);
});

</script>

<template>
    <div ref="containerDiv" class="dialog-container">
        <div class="disabler" :class="{ blink }" v-if="isModal"></div>
        <div ref="dialogDiv" class="dialog" :class="{ hasTitle, hasButton, resizable: _resizable }" :tabindex="tabindex">
            <div class="body">
                <div ref="titleDiv" class="titlebar" v-if="title != null && title.length > 0">
                    <Icon :name="icon" v-if="icon != null"></Icon>
                    <i class="fa fa-edit"></i>
                    <span class="titleText">{{ title }}</span>
                </div>
                <slot name="content">
                    <div class="content">
                        <slot> Example Dialog Content </slot>
                    </div>
                </slot>
                <slot name="commands" v-if="hasButton">
                    <div class="commands flex-row" v-if="containerDiv != null">
                        <CmdButtons :src="buttons" :target="containerDiv" :runner="run" pos="left" />
                        <div class="filler"></div>
                        <CmdButtons :src="buttons" :target="containerDiv" :runner="run" pos="right" />
                    </div>
                </slot>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.dialog-container {
    display: v-bind(initDisplay);

    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
}

.disabler {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    background: rgba(128, 128, 128, .6);

    &.blink {
        cursor: wait;
        animation: bgloop 1.3s infinite alternate ease-in-out;
    }

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
}

.dialog {
    display: flex;
    flex-direction: row;

    position: absolute;
    top: 50%;
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    overflow: auto;
    box-sizing: border-box;

    // width: 100%;
    height: v-bind(height);
    max-width: 90%;
    max-height: 80%;

    --frame-color: hsl(200, 30%, 90%);

    background: #fff;
    border-style: solid;
    border-width: 2px;
    border-color: hsl(200, 30%, 50%);
    border-radius: .4em;
    box-shadow: 3px 3px 10px 0px gray;

    &:focus-visible {
        outline: none;
    }

    &.resizable {
        resize: both;
    }

    .titlebar {
        background-color: var(--frame-color);
    }

    >.body {
        display: flex;
        flex-direction: column;
        overflow: hidden;
        // box-sizing: border-box;
        flex: 1;

        .titlebar {
            flex: 0 0 auto;
            cursor: pointer;
            user-select: none;
        }

        .commands {
            flex: 0 0 auto;
        }
    }
}

iframe {
    border: 0;
}

.titlebar {
    border-bottom: solid 1px gray;
    padding: .5em;
    font-size: 110%;
    font-weight: bold;
    text-align: center;
}

.content {
    overflow: auto;
    flex: 1;
    padding: 1em;
}

.commands {
    margin: 0;
    padding: .2em .5em;
    background: #fafbf3;
    border-top: solid 1px gray;
    justify-content: flex-end;
    list-style: none;
}

.filler {
    flex: 1;
}
</style>
