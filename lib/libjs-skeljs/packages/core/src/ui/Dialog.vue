<script setup lang="ts">

import $ from 'jquery';

import { computed, onMounted, ref } from "vue";
import { resolveChild } from "../dom/create";
import { bool, Command, dialogCmds } from './types';

import Icon from './Icon.vue';
import CmdButtons from './CmdButtons.vue';

const modelValue = defineModel();

interface Props {
    group?: string
    modal?: boolean | string

    icon?: string
    title?: string

    closable?: boolean | string
    maximizable?: boolean | string
    minimizable?: boolean | string

    buttons?: Command[]

    width?: string | number
    height?: string | number
    center?: string

    autoOpen?: boolean | string
}

const props = withDefaults(defineProps<Props>(), {
    closable: true,
    maximizable: false,
    minimizable: false,
    buttons: [dialogCmds.close] as any,
    center: 'window',
    autoOpen: false,
});

const blink = computed(() => props.modal == 'blink');
const isModal = computed(() => bool(props.modal) || props.modal == 'blink');
const isClosable = computed(() => bool(props.closable));
const isMaximizable = computed(() => bool(props.maximizable));
const isMinimizable = computed(() => bool(props.minimizable));
const isAutoOpen = computed(() => bool(props.autoOpen));

const initDisplay = computed(() => isAutoOpen.value ? 'block' : 'none');

interface Emits {
    (e: 'created', event: Event): void
    (e: 'closed', event: Event): void
    (e: 'select', value: any): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();

onMounted(() => {
    let dialogs = resolveChild(document.body, '.dialogs');
    if (props.group != null) {
        let groupId = '#dialog-group-' + props.group;
        dialogs = resolveChild(dialogs, groupId, true);
    }
    dialogs.appendChild(rootElement.value!);
});

function open() {
    $(rootElement.value!).show();
}

function run(button: Command, event?: Event) {
    switch (button.action) {
        case 'close':
            $(rootElement.value!).hide();
            return;

        case 'maximize':

    }

    if (button.run != null)
        button.run(event);
}

defineExpose({
    open
});
</script>

<template>
    <div ref="rootElement" class="dialog-with-fx">
        <div class="disabler" :class="{ blink }" v-if="isModal"></div>
        <div class="dialog">
            <slot name="body">
                <div class="body">
                    <slot name="titlebar">
                        <div class="titlebar">
                            <Icon :name="icon" v-if="icon != null"></Icon>
                            <i class="fa fa-edit"></i>
                            {{ title }}
                        </div>
                    </slot>

                    <slot name="content">
                        <div class="content">
                            <slot>
                                Example Dialog Content
                            </slot>
                        </div>
                    </slot>

                    <slot name="commands">
                        <div class="commands flex-row" v-if="buttons != null && rootElement != null">
                            <CmdButtons :src="buttons" :target="rootElement" :runner="run" pos="left" />
                            <div class="filler"></div>
                            <CmdButtons :src="buttons" :target="rootElement" :runner="run" pos="right" />
                        </div>
                    </slot>
                </div>
            </slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.dialog-with-fx {
    display: v-bind(initDisplay);
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

    // width: 100%;
    max-width: 90%;
    max-height: 80%;

    background: #fff;
    border: solid 5px hsla(200, 30%, 30%, .5);
    border-radius: 5px;
    box-shadow: 3px 3px 10px 0px gray;

    >.body {
        display: flex;
        flex-direction: column;
        overflow: hidden;
        // box-sizing: border-box;

        .titlebar {
            flex: 0 0 auto;
        }

        .body {
            flex: 1;
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
    background: #e0eef0;
    border-bottom: solid 1px gray;
    border-radius: 5px 5px 0 0;
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
