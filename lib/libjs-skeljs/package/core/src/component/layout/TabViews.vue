<script setup lang="ts">

import "../../skel/skel.scss";

import NavBar from '../menu/NavBar.vue';
import type { Menu } from "../menu/menu";

interface Props {
    views: Menu
}

const view = defineModel('view');
const props = defineProps<Props>();
const emit = defineEmits<{
    (e: 'viewChanged', newView: string, oldView: string): void
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