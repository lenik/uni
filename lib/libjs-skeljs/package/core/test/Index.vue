<script setup lang="ts">

import { ref } from 'vue';

import type { Menu } from '../src/component/menu/menu';
import { getSelection } from '../src/component/menu/menu';

import TabViews from '../src/component/layout/TabViews.vue';
import View from '../src/component/layout/View.vue';
import NodeTree from '../src/component/tree/NodeTree.vue';

import { root } from './router';

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
