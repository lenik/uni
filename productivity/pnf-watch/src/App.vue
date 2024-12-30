<script setup lang="ts">

import { provide, ref } from 'vue';

import ModelC from 'skel01-core/src/ui/layout/ModelC.vue';
import SiteBar from 'skel01-core/src/ui/menu/SiteBar.vue';
import ProjectInfo from 'skel01-core/src/ui/demo/ProjectInfo.vue';
import Copyright from 'skel01-core/src/ui/demo/Copyright.vue';
import { SERVER_URL } from 'skel01-dba/src/ui/lily/context';
import { serverUrl } from './server.config';

const model = defineModel();

interface Props {
}

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// provides
provide(SERVER_URL, serverUrl);

// property shortcuts

const stat = {
    views: 100,
    stars: 50,
    upvotes: 80,
    downvotes: 2,
};

</script>

<template>
    <ModelC>
        <template #site-bar>
            <SiteBar title="PnF Watch" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="PnF Watch"
                description="Task dashboard and multiple timers">
            </ProjectInfo>
        </template>

        <router-view></router-view>

        <template #footer>
            <Copyright />
        </template>
    </ModelC>
</template>

<style lang="scss" scoped>
</style>
