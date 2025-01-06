<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import type { Command } from './types';
import { bool } from './types';

import Icon from './Icon.vue';

const modelValue = defineModel();

interface Props {
    cmd?: Command
    label?: string
    icon?: string

    tagName?: string
    target?: any

    showIcon?: boolean | string
    showLabel?: boolean | string
    showBorder?: boolean | string
}

const props = withDefaults(defineProps<Props>(), {
    // cmd: { name: 'unspecified' } as Command,
    tagName: 'div',
    showIcon: true,
    showLabel: true,
    showBorder: true,
});

const withIcon = computed(() => bool(props.showIcon));
const withLabel = computed(() => bool(props.showLabel));
const withBorder = computed(() => bool(props.showBorder));

const _cmd = computed(() => props.cmd || { name: 'unspecified' });

const button = computed(() => _cmd.value.type == null || _cmd.value.type == 'button');
const enabled = computed(() => _cmd.value.enabled != false);
const disabled = computed(() => _cmd.value.enabled == false);

const toggler = computed(() => _cmd.value.type == 'toggle');
const checked = computed(() => (_cmd.value.type == 'toggle' && _cmd.value.checked));
const unchecked = computed(() => (_cmd.value.type == 'toggle' && !_cmd.value.checked));

interface Emits {
    (e: 'created', event: Event): void
    (e: 'checked', event: Event): void
    (e: 'unchecked', event: Event): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement | null>();

onMounted(() => {
});

function onclick(event: Event) {
    let cmd = _cmd.value;
    switch (cmd.type) {
        case 'toggle':
            cmd.checked = !cmd.checked;
            if (cmd.checked)
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
            <component :is="_cmd.href != null ? 'a' : 'div'"
                :class="{ withIcon, withLabel, withBorder, button, toggler, enabled, disabled, checked, unchecked }"
                :name="_cmd.name" :href="_cmd.href" :title="_cmd.tooltip" @click="(e: any) => onclick(e)">
                <div class="hover"></div>
                <Icon :name="(icon || _cmd.icon)!" v-if="withIcon && (icon != null || _cmd.icon != null)" />
                <span class="sep" v-if="withIcon && withLabel"></span>
                <span class="label" v-if="withLabel">{{ label || _cmd.label || _cmd.name }}</span>
            </component>
            <Icon name="fa-ques" v-if="_cmd.description != null" />
        </div>
        <div class="description" v-if="_cmd.description != null"> {{ _cmd.description }} </div>
    </component>
</template>

<style scoped lang="scss">
.cmd-button {
    padding: inherit;
    display: inline-block;
}

.button.disabled {
    color: gray;
}

.button,
.toggler {
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

.toggler {
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
