<script setup lang="ts">

import { computed, ref, watch } from 'vue';

import type { UiNode } from '../ui-node';

import { defaultTyper, type NodeTyperFunc } from './typer';

import Icon from '../Icon.vue';

interface Props {
    node: UiNode<any>
    _key?: string
    path?: string
    depth?: number
    maxDepth?: number
    // expandable?: boolean
    // expanded?: boolean
    typer?: NodeTyperFunc
}

const model = defineModel<UiNode<any>>();
const viewData = defineModel<any>('data');

const props = withDefaults(defineProps<Props>(), {
    name: undefined,
    path: '',
    depth: 0,
    maxDepth: Number.MAX_VALUE,
    // expandable: true,
    // expanded: true,
    typer: defaultTyper,
});

const emit = defineEmits<{
}>();

const nodeValue = computed(() => props.node[0]);
const children = computed(() => props.node[1]);
const childKeys = computed(() => children.value == null ? [] : Object.getOwnPropertyNames(children.value));
const childCount = computed(() => childKeys.value.length);
const options = computed(() => props.node[2]);

const realExpandable = computed(() => {
    if (options.value?.expandable === false)
        return false;
    if (props.expandable != null)
        return props.expandable;
    if (childCount.value == 0)
        return false;
    return true;
});

const realExpanded = computed(() => {
    if (options.value?.expanded != null)
        return options.value.expanded;
    // if (props.expanded != null)
    //     return props.expanded;
    return 'unknown';
});

const showChildren = computed(() => {
    // depth < maxDepth && children != null
    if (props.depth > props.maxDepth)
        return false;
    if (childCount.value == 0)
        return false;
    if (realExpanded.value === false)
        return false;
    return true;
});

const prefix = computed(() => {
    let path = props.path;
    if (path.endsWith('/'))
        return path;
    if (path.length == 0)
        return '';
    return path + '/';
});

const classMap = computed(() => { 
    return {
        'root': props.depth == 0,
        'leaf': children.value == null || childCount.value == 0,
        'expanded': options.value?.expanded !== false,
        'collapsed': options.value?.expanded === false,
        'expandable': props.expandable && childCount.value != 0,
    }
});

const style = computed((): CSSStyleDeclaration => options.value?.style || {});

const listStyle = computed(() => options.value?.listStyle);

const tagName = computed(() => {
    switch (listStyle.value) {
        case 'decimal':
            return 'ol';
    }
    return 'ul';
})

const rootElement = ref();

function toggleExpanded() {
    if (! Array.isArray(props.node)) {
        // console.debug("convert node to tuple-node");
        props.node = [ props.node ];
    }
    
    let node = props.node;
    if (node[2] == null) {
        // console.debug("create options");
        node[2] = {};
    }
    let opts = node[2];

    if (opts.expanded == null)
        opts.expanded = false;
    else
        opts.expanded = !opts.expanded;
}

defineExpose({
    toggleExpanded,
});
</script>

<template>
    <div ref="rootElement" class="tree-node" :class="classMap" :depth="depth">
        <span class="toggler" v-if="realExpandable" @click="toggleExpanded">
            <Icon name="fa-chevron-down" v-if="realExpanded" />
            <Icon name="fa-chevron-right" v-else />
        </span>

        <slot :node="node" :value="nodeValue" :path="path" :_key="_key" :depth="depth"
                :toggleExpanded="toggleExpanded">
             {{ nodeValue }}
        </slot>
        
        <component :is="tagName" v-if="showChildren">
            <template v-for="(child, k) in children" :key="k + ''">
                <template v-for="(childOpts, _i) in [child[2]]" :key="_i">
                    <li v-if="childOpts?.visible !== false">
                        <node-tree :node="child" :depth="depth + 1" :_key="k + ''" :path="prefix + k"
                                :expandable="childOpts?.expandable" :expanded="childOpts?.expanded">
                            <template v-for="(_, slotName) of $slots" v-slot:[slotName]="slotProps" :key="slotName">
                                <component :is="$slots[slotName]!(slotProps)[0]" />
                            </template>
                        </node-tree>
                    </li>
                </template>
            </template>
        </component>
        <slot name="post" :node="node" :value="nodeValue" :path="path" :_key="_key" :depth="depth"></slot>
    </div>
</template>

<style lang="scss" scoped>
.toggler {
    font-size: 50%;
    user-select: none;
    cursor: pointer;
    vertical-align: middle;
    margin-right: 0.5em;
    padding: 0.3em;
    // background-color: #eee;
    // border-radius: 1em;
}
</style>
