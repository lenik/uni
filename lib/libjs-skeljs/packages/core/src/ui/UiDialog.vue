<script setup lang="ts">

import $ from 'jquery';
import "@eirslett/jquery-ui-esm/esm/widgets/dialog";
import 'jquery-ui/themes/base/base.css';

import { onMounted, ref } from "vue";
import { resolveChild } from "../dom/create";
import { parseLen } from '../util/measure';

const modelValue = defineModel();

interface Props {
    group?: string
    modal?: boolean
    title?: string;
    width?: string | number;
    height?: string | number;
    center?: string
}

const props = withDefaults(defineProps<Props>(), {
    center: 'window'
});

interface Emits {
    (e: 'created', event: Event): void
    (e: 'closed', event: Event): void
    (e: 'select', value: any): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();

var $dialog;

onMounted(() => {
    let dialogs = resolveChild(document.body, '.dialogs');
    if (props.group != null) {
        let groupId = '#dialog-group-' + props.group;
        dialogs = resolveChild(dialogs, groupId, true);
    }
    dialogs.appendChild(rootElement.value!);

    $dialog = $(rootElement.value!);

    $dialog.dialog({
        autoOpen: false,
        create: function (event: Event, ui: any) {
            let parent = $(event.target!).parent();
            console.log(parent);
            console.log(ui);
            parent.css('position', 'fixed');
            emit('created', event);
        },
        close: function (event: Event, ui: any) {
            emit('closed', event);
        }
    });

    update();
});

function update() {
    const defaultWidth = 0;
    const defaultHeight = 0;
    let options: any = {
        modal: props.modal,
        title: props.title
    };

    switch (props.center) {
        case 'window':
            options.position = {
                my: "center",
                at: "center",
                of: window,
                collision: "none"
            };
            break;
    }

    if (props.width != null) {
        const parentWidth = $(window).width() || 0;
        options.width = parseLen(props.width, parentWidth, defaultWidth);
    }
    if (props.height != null) {
        const parentHeight = $(window).height() || 0;
        options.height = parseLen(props.height, parentHeight, defaultHeight);
    }

    options.buttons = [{
        text: "Close",
        click: function () {
            $dialog.dialog('close');
        }
    }];

    $dialog.dialog('option', options);
}

function open() {
    update();
    $dialog.dialog('open');
}

defineExpose({
    open
});
</script>

<template>
    <div ref="rootElement" class="component">
        <slot>
        </slot>
        <slot name="buttons"></slot>
    </div>
</template>

<style scoped lang="scss">
.component {
}
</style>
