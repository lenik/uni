<script setup lang="ts">

import { provide, ref } from 'vue';

import ModelC from '@skeljs/core/src/ui/layout/ModelC.vue';
import SiteBar from '@skeljs/core/src/ui/menu/SiteBar.vue';
import ProjectInfo from '@skeljs/core/src/ui/demo/ProjectInfo.vue';
import Copyright from '@skeljs/core/src/ui/demo/Copyright.vue';
import { SERVER_URL } from '../src/ui/lily/context';
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
    stars: 5,
    upvotes: 26,
    downvotes: 2,
};

</script>

<template>
    <ModelC>

        <template #site-bar>
            <SiteBar title="DBA Examples" />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="DBA Test Apps">
                <p> Test applications of skeljs::dba package. </p>
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

.main {
    display: flex;
    flex-direction: column;
    width: 100%;
}
</style>