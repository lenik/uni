<script setup lang="ts">

import { ref } from 'vue';

import type { Menu } from '@skeljs/core/src/ui/menu/menu';
import { getSelection } from '@skeljs/core/src/ui/menu/menu';

import TabViews from '@skeljs/core/src/ui/layout/TabViews.vue';
import View from '@skeljs/core/src/ui/layout/View.vue';
import NodeTree from '@skeljs/core/src/ui/tree/NodeTree.vue';

const views: Menu = {
    dtn: {
        selected: true,
        label: "DataTable",
        iconfa: "table",
    },
};

const initialView = getSelection(views)[0];
const view = ref(initialView);

</script>

<template>
    <TabViews :views="views" v-model:view="view">
        <View name="dtn" :selection="view">
            <node-tree :node="root">
                <template v-slot="{ path, node }">
                    <router-link :to="path">
                        {{ node[2]?.label || path }}
                    </router-link>
                </template>
            </node-tree>
        </View>
    </TabViews>
</template>
