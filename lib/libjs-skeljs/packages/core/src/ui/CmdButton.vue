<script setup lang="ts">

import $ from 'jquery';

import { onMounted, ref } from "vue";
import { Command } from './types';

import Icon from './Icon.vue';

const modelValue = defineModel();

interface Props {
    cmd: Command
    tagName?: string
    target?: any
}

const props = withDefaults(defineProps<Props>(), {
    tagName: 'div'
});

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
    <component :is="tagName" ref="rootElement" class="component">
        <div class="btn-with-extras">
            <component :is="cmd.href != null ? 'a' : 'div'" class="btn" :href="cmd.href" :title="cmd.tooltip">

                <Icon :name="cmd.icon" v-if="cmd.icon != null" />

                <span class="label">{{ cmd.label }}</span>

            </component>

            <Icon name="fa-ques" v-if="cmd.description != null" />
        </div>

        <div class="description" v-if="cmd.description != null">
            {{ cmd.description }}
        </div>
    </component>
</template>

<style scoped lang="scss">
.component {}

.btn {
    display: flex;
    flex-direction: row;
    align-items: center;

    margin: 3px;
    padding: 4px 8px;

    background-color: hsl(190, 45%, 85%);
    border-radius: 5px;
    border-style: solid;
    border-width: 1px;
    border-color: hsl(190, 45%, 30%);
    cursor: pointer;

    &:hover {
        background-color: hsl(190, 45%, 95%);
    }

    .icon {
        margin-right: .5em;
    }

    .label {
        font-weight: 300;
    }
}

.description {
    display: none;
}
</style>
