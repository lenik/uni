<script setup lang="ts">

import { ref } from 'vue';

import ModelC from '@skeljs/core/src/ui/layout/ModelC.vue';
import SiteBar from '@skeljs/core/src/ui/menu/SiteBar.vue';
import ProjectInfo from '@skeljs/core/src/ui/demo/ProjectInfo.vue';

import { showError } from "@skeljs/core/src/logging/api";
import { } from "./types";

const siteMenu = [
    { href: "./index.html", iconfa: "fa-file-o", label: "This" },
    { href: "about:blank", iconfa: "fa-modx", label: "Blank" },
];

const version = "1.0";
const stat = {
    views: 0,
    stars: 0,
    upvotes: 0,
    downvotes: 0,
};

</script>

<template>
    <ModelC>
        <template #site-bar>
            <SiteBar :items="siteMenu" title="Example App" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="DBA Test Apps">
                Example Application generated with newcomp
            </ProjectInfo>
        </template>

        <router-view></router-view>

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
</style>
