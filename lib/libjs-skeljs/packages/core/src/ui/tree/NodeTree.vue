<script setup lang="ts">

import { computed, ref, watch } from 'vue';

import type { UiNode } from '../ui-node';

import { defaultTyper, type NodeTyperFunc } from './typer';

interface Props {
    node: UiNode<any>
    _key?: string
    path?: string
    depth?: number
    maxDepth?: number
    typer?: NodeTyperFunc
}

const model = defineModel<UiNode<any>>();
const viewData = defineModel<any>('data');

const props = withDefaults(defineProps<Props>(), {
    name: undefined,
    path: '',
    depth: 0,
    maxDepth: Number.MAX_VALUE,
    typer: defaultTyper,
});

const emit = defineEmits<{
}>();

const [value, children, options] =
    Array.isArray(props.node) ? props.node : [props.node];

const prefix = computed(() => {
    let path = props.path;
    if (path.endsWith('/'))
        return path;
    if (path.length == 0)
        return '';
    return path + '/';
});

const style = computed((): CSSStyleDeclaration => {
    return options == null ? {} : (options.style || {})
});
const listStyle = computed((): string | undefined => {
    return options == null ? undefined : options.listStyle
});
const tagName = computed(() => {
    switch (listStyle.value) {
        case 'decimal':
            return 'ol';
    }
    return 'ul';
})

const rootElement = ref();

</script>

<template>
    <div ref="rootElement" class="tree-node" :class="{ 'root-node': depth == 0 }" :depth="depth">
        <slot :node="node" :value="value" :path="path" :_key="_key" :depth="depth"> {{ value }} </slot>
        <component :is="tagName" v-if="depth < maxDepth && children != null">
            <li v-for="(child, k) in children" :key="k + ''">
                <node-tree :node="child" :depth="depth + 1" :_key="k + ''" :path="prefix + k">
                    <template v-for="(_, slotName) of $slots" v-slot:[slotName]="slotProps" :key="slotName">
                        <component :is="$slots[slotName]!(slotProps)[0]" />
                    </template>
                </node-tree>
            </li>
        </component>
    </div>
</template>

<style lang="scss" scoped></style>
