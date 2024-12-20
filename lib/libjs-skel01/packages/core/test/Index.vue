<script lang="ts">
import { computed, ref } from 'vue';
import { links } from './router';

import type { ViewHandles } from '../src/ui/layout/TabViews.vue';
import TabViews, { getSelectedViewName } from '../src/ui/layout/TabViews.vue';
import View from '../src/ui/layout/View.vue';

export const title = 'List of Test Samples';

const views: ViewHandles = {
    index: {
        selected: true,
        label: "Directory",
        icon: "far-list",
    },
};

const initialView = getSelectedViewName(views);
</script>

<script setup lang="ts">
const view = ref(initialView);
</script>

<template>
    <TabViews :views="views" v-model:view="view">
        <View name="index" :selection="view">
            <ul>
                <li v-for="(it, k) in links" :key="k">
                    <router-link :to="k"> {{ it }} </router-link>
                </li>
            </ul>
        </View>
    </TabViews>
</template>./router-bak
