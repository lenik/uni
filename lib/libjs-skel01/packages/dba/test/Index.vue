<script lang="ts">
import { ref } from 'vue';

import type { ViewHandles } from '@skel01/core/src/ui/layout/TabViews.vue';
import TabViews, { getSelectedViewName } from '@skel01/core/src/ui/layout/TabViews.vue';
import View from '@skel01/core/src/ui/layout/View.vue';

import { links } from './router';

export const title = 'Directory';
</script>

<script setup lang="ts">
const views: ViewHandles = {
    index: {
        selected: true,
        label: "Directory",
        icon: "far-list",
    },
};

const initialView = getSelectedViewName(views);
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
</template>