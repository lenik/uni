<script lang="ts">
import { ref } from 'vue';

export const title = 'Core Test Site'
</script>

<script setup lang="ts">
import ModelC from '../src/ui/layout/ModelC.vue';
import SiteBar from '../src/ui/menu/SiteBar.vue';
import ProjectInfo from '../src/ui/demo/ProjectInfo.vue';
import Copyright from '../src/ui/demo/Copyright.vue';

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
            <SiteBar :items="siteMenu" title="Title1" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="Test Apps">
                <p> Test applications of skel01::core package. </p>
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