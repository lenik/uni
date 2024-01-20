<script setup lang="ts">

import { ref } from 'vue';

import ModelC from '@skeljs/core/src/ui/layout/ModelC.vue';
import SiteBar from '@skeljs/core/src/ui/menu/SiteBar.vue';
import ProjectInfo from '@skeljs/core/src/ui/demo/ProjectInfo.vue';
import Copyright from '@skeljs/core/src/ui/demo/Copyright.vue';

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

</script>

<template>
    <ModelC>
        <template #site-bar>
            <SiteBar :items="siteMenu" :title="'Title1'" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="DBA Test Apps">
                Test applications of skeljs::dba package.
            </ProjectInfo>
        </template>

        <router-view></router-view>

        <template #footer>
            <Copyright />
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