<script setup lang="ts">

import { ref } from 'vue';

import NormalApp from 'skeljs/component/NormalApp.vue';
import SiteBar from '../component/SiteBar.vue';
import ProjectInfo from '../component/ProjectInfo.vue';
import About from './About.vue';
import Options from './Options.vue';

import * as site from "../site.js";
const siteModel = site.model;

const version = "1.0";
const stat = {
    views: 0,
    stars: 0,
    upvotes: 0,
    downvotes: 0,
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

</script>

<template>
    <NormalApp :views="views" v-model:view="view">
        <template #site-bar>
            <SiteBar id="sitebar"/>
        </template>

        <template #proj-info>
            <ProjectInfo id="proj-info"/>
        </template>

        <template #views>
            <About id="v-about" :class="{ selected: view == 'about' }" :version="version" />
            <Options id="v-options" :class="{ selected: view == 'options' }" />
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
    </NormalApp>

</template>