<script setup lang="ts">

import { ref } from 'vue';

import NotImpl from '../src/ui/demo/NotImpl.vue';
import TabViews, { getSelectedViewName, ViewHandles } from '../src/ui/layout/TabViews.vue';
import View from '../src/ui/layout/View.vue';

const views: ViewHandles = {
    about: {
        label: "About",
        icon: "fa-info",
    },
    options: {
        selected: true,
        label: "Options",
        icon: "fa-cog",
    },
};

const initialView = getSelectedViewName(views);
const view = ref(initialView);

function msg(s: string) {
    console.log(s);
}

</script>

<template>
    <TabViews :views="views" v-model:view="view">
        <View name="about" :selection="view" @enter="msg('enter about')" @leave="msg('leave about')">
            <div> about </div>
        </View>
        <View name="options" :selection="view">
            <NotImpl />
        </View>
    </TabViews>
</template>

<style lang="scss" scoped></style>
