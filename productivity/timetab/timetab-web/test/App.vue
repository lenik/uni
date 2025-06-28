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
provide(SERVER_URL,  (window as any).serverUrl || serverUrl);

// property shortcuts

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
            <SiteBar title='Lily tests' />
        </template>

        <template #project-info>
            <ProjectInfo :stat="stat" label="Lily Test Apps" description="Test applications of lily::basic. ">
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