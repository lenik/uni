<script setup lang="ts">

import { ref } from 'vue';

import ModelC from '@/component/layout/ModelC.vue';
import SiteBar from '@/component/menu/SiteBar.vue';
import ProjectInfo from '@/component/demo/ProjectInfo.vue';
import NotImpl from '@/component/demo/NotImpl.vue';
import TabViews from '@/component/layout/TabViews.vue';
import View from '@/component/layout/View.vue';

import { Menu, getSelection } from '@/component/menu/menu';

const siteMenu = [
    { href: "./index.html", iconfa: "fa-file-o", label: "This" },
    { href: "about:blank", iconfa: "fa-modx", label: "Blank" },
];

const version = "1.0";
const stat = {
    views: 100,
    stars: 5,
    upvotes: 26,
    downvotes: 2,
};

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
    <ModelC>
        <template #site-bar>
            <SiteBar :items="siteMenu" :title="'Title1'" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="Layout Test Page">
                An example page using skeljs-core ModelC layout.
            </ProjectInfo>
        </template>

        <TabViews :views="views" v-model:view="view">
            <View name="about" :selection="view" @enter="msg('enter about')" @leave="msg('leave about')">
                about
            </View>
            <View name="options" :selection="view">
                <NotImpl></NotImpl>
            </View>
        </TabViews>

        <template #footer>
            <div id="footbar" style="display: flex; flex-direction: row">
                <div class="copyright" style="flex: 50">
                    (ↄ) Jun 2017 @_lenik
                </div>
                <div id="statcounter" style="flex: 50">
                    <img class="statcounter">
                </div>
            </div>
        </template>
    </ModelC>
</template>

<style lang="scss" scoped>
.sitebar {
    background: #444;
}

#v-options {
    padding: 0;
}
</style>