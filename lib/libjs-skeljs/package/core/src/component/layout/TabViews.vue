<script setup lang="ts">

import "../../skel/skel.scss";

import NavBar from '../nav/NavBar.vue';

interface Props {
    views: any
}

const view = defineModel('view');
const props = defineProps<Props>();
const emit = defineEmits<{
    (e: 'viewChanged', newView: string, oldView: string): void
}>();

</script>

<template>
    <div class="flex-column full-size">
        <NavBar :items="views" v-model="view"
            @view-changed="(n, o) => $emit('viewChanged', n, o)" />
        <div class="stack flex-1">
            <slot></slot>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.stack::v-deep() {
    position: relative;

    > div:not(.iscroll-wrapper), > .iscroll-wrapper > div {
        position: absolute;
        padding: 0;
        top: 0;
        right: 0;
        left: 0;
        bottom: 0;
        overflow: auto;
  
        padding: 1em;
    }

    > :not(.selected) {
        // display: none;
        visibility: hidden;
    }
}

</style>