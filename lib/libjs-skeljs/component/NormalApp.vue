<script setup lang="ts">

import "../css/skel.scss";

import NavBar from './NavBar.vue';

const view = defineModel('view');

defineProps<{
    views: any
}>();

</script>

<template>
    <div class="flex-column full-size">

        <div id="top" class="clear-each">
            <slot name="site-bar"></slot>

            <slot name="project-info"></slot>
        </div>

        <div id="view-container" class="flex-1 flex-column">

            <NavBar :items="views" v-model="view" />

            <div id="views" class="stack">
                <slot name="views"></slot>
            </div>

        </div>

        <slot name="footer"></slot>

    </div>
</template>