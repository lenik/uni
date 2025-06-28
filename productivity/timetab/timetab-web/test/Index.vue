<script setup lang="ts">

import { ref } from 'vue';

import type { ViewHandles } from 'skel01-core/src/ui/layout/TabViews.vue';
import TabViews, { getSelectedViewName } from 'skel01-core/src/ui/layout/TabViews.vue';
import View from 'skel01-core/src/ui/layout/View.vue';
import { links } from './router';
import { links as basicLinks } from 'lily-basic/test/router';

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
            <ol>
                <li>
                    <h3>timetab entities:</h3>
                    <ol>
                        <li v-for="(it, k) in links" :key="k">
                            <router-link :to="k"> {{ it }} </router-link>
                        </li>
                    </ol>
                </li>
                <li>
                    <h3>lily/basic entities:</h3>
                    <ol>
                        <li v-for="(it, k) in basicLinks" :key="k">
                            <router-link :to="k"> {{ it }} </router-link>
                        </li>
                    </ol>
                </li>
            </ol>
        </View>
    </TabViews>
</template>

<style type="scss" scoped>
#v-index {
    columns: 1;
}
</style>
