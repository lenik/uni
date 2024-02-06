<script setup lang="ts">

import { ref } from 'vue';

import type { ViewHandles } from '@skeljs/core/src/ui/layout/TabViews.vue';
import TabViews, { getSelectedViewName } from '@skeljs/core/src/ui/layout/TabViews.vue';
import View from '@skeljs/core/src/ui/layout/View.vue';
import NodeTree from '@skeljs/core/src/ui/tree/NodeTree.vue';

import { root } from './router';

const views: ViewHandles = {
    dtn: {
        selected: true,
        label: "DataTable",
        icon: "far-table",
    },
};

const initialView = getSelectedViewName(views);
const view = ref(initialView);

</script>

<template>
    <TabViews :views="views" v-model:view="view">
        <View name="dtn" :selection="view">
            <NodeTree :node="root">
                <template v-slot="{ path, node }">
                    <router-link :to="path"> {{ node[2]?.label || path }} </router-link>
                </template>
            </NodeTree>
        </View>
    </TabViews>
</template>