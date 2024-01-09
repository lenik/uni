<script setup lang="ts">

import { ref } from 'vue';

import type { Menu } from 'skeljs-core/src/component/menu/menu';
import { getSelection } from 'skeljs-core/src/component/menu/menu';

import TabViews from 'skeljs-core/src/component/layout/TabViews.vue';
import View from 'skeljs-core/src/component/layout/View.vue';

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
                <li><router-link to="objv">Data Var</router-link></li>
                <li><router-link to="ajax">Ajax data</router-link></li>
            </ul>
        </View>
    </TabViews>
</template>
