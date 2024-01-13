<script setup lang="ts">

import $ from 'jquery';

import { onMounted, ref } from "vue";
import { resolveChild } from "../dom/create";
import { Command } from './types';

import Icon from './Icon.vue';
import CmdButton from './CmdButton.vue';

const modelValue = defineModel();

interface Props {
    group?: string
    modal?: boolean

    icon?: string
    title?: string

    closable?: boolean
    maximizable?: boolean
    minimizable?: boolean

    button?: Command
    buttons?: Command[]
    buttonsToLeft?: Command[]

    width?: string | number
    height?: string | number
    center?: string
}

const props = withDefaults(defineProps<Props>(), {
    closable: true,
    maximizable: false,
    minimizable: false,

    buttons: [{
        icon: 'fa-close',
        label: 'Close',
        action: 'close',
        description: 'Close the dialog.',
        tooltip: 'Click on this button to close the current dialog.',
    }],

    center: 'window',
});

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
    <div ref="rootElement" class="component dialog">
        <div class="flex-column">
            <slot name="titlebar">
                <div class="titlebar">
                    <Icon :name="icon" v-if="icon != null"></Icon>
                    <i class="fa fa-edit"></i>
                    {{ title }}
                </div>
            </slot>


            <slot name="body">
                <div class="body">
                    <slot>
                        Example Dialog Content
                    </slot>
                </div>
            </slot>

            <slot name="commands">
                <ul class="commands flex-row">
                    <li v-for="(button, i) in buttonsToLeft" :key="i">
                        <CmdButton :cmd="button" :target="rootElement" @click="run(button, event)"></CmdButton>
                    </li>
                    <li class="filler"></li>
                    <li v-for="(button, i) in buttons" :key="i">
                        <CmdButton :cmd="button" :target="rootElement" @click="run(button, event)"></CmdButton>
                    </li>
                </ul>
            </slot>
        </div>
    </div>
</template>

<style  lang="scss">
.dialogs {
    // position: fixed;
    // top: 0;
    // bottom: 0;
    // left: 0;
    // right: 0;
}
</style>

<style scoped lang="scss">
.component {}

.dialog {
    display: none;

    position: absolute;
    top: 50%;
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    
    max-width: 80%;
    max-height: 80%;

    background: #fff;
    border: solid 5px hsla(200, 30%, 30%, .5);
    border-radius: 5px;
    box-shadow: 3px 3px 10px 0px gray;
    overflow: hidden;

    iframe {
        border: 0;
    }

    >.flex-column {
        height: 100%;

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

    .titlebar {
        background: #e0eef0;
        border-bottom: solid 1px gray;
        border-radius: 5px 5px 0 0;
        padding: .5em;
        font-size: 110%;
        font-weight: bold;
        text-align: center;
    }

    .body {
        overflow: scroll;
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

}

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
