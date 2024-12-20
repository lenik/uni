<script setup lang="ts">

import { ref } from 'vue';

import type { ViewHandles } from '@skel01/core/src/ui/layout/TabViews.vue';
import TabViews, { getSelectedViewName } from '@skel01/core/src/ui/layout/TabViews.vue';
import View from '@skel01/core/src/ui/layout/View.vue';
import NodeTree from '@skel01/core/src/ui/tree/NodeTree.vue';

import { root } from './router';

const views: ViewHandles = {
    index: {
        selected: true,
        label: "Index",
        icon: "fa-list",
    },
    main: {
        label: "Lunar Birthday Matcher",
        icon: "far-moon",
    },
    about: {
        label: "About",
        icon: "far-info-circle",
    },
    project: {
        label: "Project",
        icon: "fab-github",
    }
};

const initialView = getSelectedViewName(views);
const view = ref(initialView);

</script>

<template>
    <TabViews :views="views" v-model:view="view">
        <View name="dtn" :selection="view">
            <node-tree :node="root">
                <template v-slot="{ path, node }">
                    <router-link :to="path"> {{ node[2]?.label || path }} </router-link>
                </template>
            </node-tree>
        </View>
    </TabViews>
</template>
