<script setup lang="ts">

import { ref } from 'vue';

import type { Menu } from '../src/component/menu/menu';
import { getSelection } from '../src/component/menu/menu';

import TabViews from '../src/component/layout/TabViews.vue';
import View from '../src/component/layout/View.vue';

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
            <ul>
                <li><router-link to="model-c">Model-C Layout</router-link></li>
                <li><router-link to="menu">Menu</router-link></li>
            </ul>
        </View>
    </TabViews>
</template>
