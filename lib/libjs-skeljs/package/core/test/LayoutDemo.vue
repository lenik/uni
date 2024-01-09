<script setup lang="ts">

import { ref } from 'vue';

import NotImpl from '@/component/demo/NotImpl.vue';
import TabViews from '@/component/layout/TabViews.vue';
import View from '@/component/layout/View.vue';

import { Menu, getSelection } from '@/component/menu/menu';

const views: Menu = {
    about: {
        label: "About",
        iconfa: "info",
    },
    options: {
        selected: true,
        label: "Options",
        iconfa: "cog",
    },
};

const initialView = getSelection(views)[0];
const view = ref(initialView);

function msg(s: string) {
    console.log(s);
}

</script>

<template>
    <TabViews :views="views" v-model:view="view">
        <View name="about" :selection="view" @enter="msg('enter about')" @leave="msg('leave about')">
            <div>
                about
            </div>
        </View>
        <View name="options" :selection="view">
            <NotImpl></NotImpl>
        </View>
    </TabViews>
</template>

<style lang="scss" scoped></style>