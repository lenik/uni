<script lang="ts">
import "../../skel/skel.scss";

import NavBar from '../menu/NavBar.vue';

export interface ViewHandle {
    priotity?: number
    label: string
    icon?: string
    checked?: boolean
    selected?: boolean
}

export interface ViewHandles {
    [viewName: string]: ViewHandle
}

export function getSelectedViewNames(handles: ViewHandles) {
    return Object.entries(handles).filter(entry => entry[1].selected).map(entry => entry[0]);
}

export function getSelectedViewName(handles: ViewHandles) {
    return getSelectedViewNames(handles)[0];
}

function compareView(a: ViewHandle, b: ViewHandle) {
    let p1: number = a.priotity || 0;
    let p2: number = a.priotity || 0;
    let cmp = p1 - p2;
    if (cmp != 0) return cmp;

    cmp = a.label.localeCompare(b.label);
    if (cmp != 0) return cmp;

    return -1;
}

export function sortViews(handles: ViewHandles): ViewHandle[] {
    let v: ViewHandle[] = Object.values(handles);
    v.sort(compareView);
    return v;
}

export interface Props {
    views: ViewHandles
}
</script>

<script setup lang="ts">
const view = defineModel<string>('view');
const props = defineProps<Props>();
const emit = defineEmits<{
    viewChanged: [newView: string, oldView: string]
}>();

</script>

<template>
    <div class="tab-view-container flex-column full-size">
        <NavBar :items="views" v-model="view" @view-changed="(n, o) => $emit('viewChanged', n, o)" />
        <div class="stack flex-1">
            <slot></slot>
        </div>
    </div>
</template>

<style lang="scss">
.view {
    >* {
        margin: 1em;
    }
}
</style>

<style lang="scss" scoped>
.tab-view-container {
    margin: 0;
}

.stack::v-deep() {
    position: relative;

    >.view:not(.iscroll-wrapper),
    >.iscroll-wrapper>.view {
        position: absolute;
        top: 0;
        right: 0;
        left: 0;
        bottom: 0;
        overflow: auto;
    }

    > :not(.selected) {
        // display: none;
        visibility: hidden;
    }
}
</style>