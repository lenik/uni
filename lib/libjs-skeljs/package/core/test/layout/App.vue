<script setup lang="ts">

import { ref } from 'vue';

import ModelC from '../../src/component/layout/ModelC.vue';
import SiteBar from '../../src/component/nav/SiteBar.vue';
import ProjectInfo from '../../src/component/demo/ProjectInfo.vue';
import NotImpl from '../../src/component/demo/NotImpl.vue';
import View from '../../src/component/layout/View.vue';

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

const view = ref("options");
const views = {
    about: {
        label: "About",
        iconfa: "info",
    },
    options: {
        label: "Options",
        iconfa: "cog",
    },
};

function msg(s) {
    console.log(s);
}

</script>

<template>
    <ModelC :views="views" v-model:view="view">
        <template #site-bar>
            <SiteBar :items="siteMenu" :title="'Title1'" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="Layout Test Page">
                An example page using skeljs-core ModelC layout.
            </ProjectInfo>
        </template>

        <template #views>
            <View name="about" :selection="view" @enter="msg('enter about')" @leave="msg('leave about')">
                about
            </View>
            <View name="options" :selection="view">
                <NotImpl />
            </View>
        </template>

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