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

const withIcon = computed(() => props.cmd.icon != null && bool(props.showIcon));
const withLabel = computed(() => props.cmd.label != null && bool(props.showLabel));
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
    <component :is="tagName" ref="rootElement" class="cmd-button">
        <div class="btn-with-extras">
            <component :is="cmd.href != null ? 'a' : 'div'" class="btn" :class="{ withIcon, withLabel, withBorder }"
                :name="cmd.name" :href="cmd.href" :title="cmd.tooltip">

                <Icon :name="cmd.icon" v-if="withIcon" />

                <span class="sep" v-if="withIcon && withLabel"></span>

                <span class="label" v-if="withLabel">{{ cmd.label }}</span>

            </component>

            <Icon name="fa-ques" v-if="cmd.description != null" />
        </div>

        <div class="description" v-if="cmd.description != null">
            {{ cmd.description }}
        </div>
    </component>
</template>

<style scoped lang="scss">
.cmd-button {}

.btn {
    display: flex;
    flex-direction: row;
    align-items: center;
    cursor: pointer;
    user-select: none;

    &.withLabel {
        margin: .3em;
        padding: .3em .8em;
    }

    &:not(.withLabel) {
        margin: .2em;
        padding: .2em;
    }

    &.withBorder {
        background-color: hsl(190, 45%, 85%);
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
        border-color: hsl(190, 45%, 30%);

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
    }

}

.description {
    display: none;
}
</style>
