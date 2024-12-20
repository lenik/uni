<script setup lang="ts">

import { ref } from 'vue';

import ModelC from '@skel01/core/src/ui/layout/ModelC.vue';
import SiteBar from '@skel01/core/src/ui/menu/SiteBar.vue';
import ProjectInfo from '@skel01/core/src/ui/demo/ProjectInfo.vue';
import Copyright from '@skel01/core/src/ui/demo/Copyright.vue';
import ImageGrid from '@skel01/core/src/ui/demo/ImageGrid.vue';
import Main from './Main.vue';

const stat = {
    views: 0,
    stars: 0,
    upvotes: 0,
    downvotes: 0,
};

const size = ref("2.5em");
</script>

<template>
    <ModelC>
        <template #site-bar>
            <SiteBar home="route-to:main" />
        </template>
        <template #project-info>
            <ProjectInfo :stat="stat" label="Lunar Birthday"
                description="在一些亚洲国家，特别是中国、韩国和越南等地，人们更加重视传统文化和习俗。阴历生日在这些文化中被认为是更为重要和吉利的。选择阴历生日可以与传统节日和庆祝活动相结合，增加庆祝的乐趣和意义。">
                <ImageGrid :random="false" :cell-width="size" :cell-height="size">
                    <img src="./img/fotor-ai-2024020512818.jpg">
                    <img src="./img/fotor-ai-2024020512858_10.jpg">
                    <img src="./img/fotor-ai-2024020512858_12.jpg">
                    <img src="./img/fotor-ai-2024020512858_13.jpg">
                    <img src="./img/fotor-ai-2024020512858_15.jpg">
                    <img src="./img/fotor-ai-2024020512858_16.jpg">
                    <img src="./img/fotor-ai-2024020512858_17.jpg">
                    <img src="./img/fotor-ai-2024020512858_18.jpg">
                    <img src="./img/fotor-ai-2024020512858_1.jpg">
                    <img src="./img/fotor-ai-2024020512858_2.jpg">
                    <img src="./img/fotor-ai-2024020512858_3.jpg">
                    <img src="./img/fotor-ai-2024020512858_4.jpg">
                    <img src="./img/fotor-ai-2024020512858_5.jpg">
                    <img src="./img/fotor-ai-2024020512858_6.jpg">
                    <img src="./img/fotor-ai-2024020512858_7.jpg">
                    <img src="./img/fotor-ai-2024020512858_9.jpg">
                    <img src="./img/fotor-ai-202402051287.jpg">
                </ImageGrid>
            </ProjectInfo>
        </template>
        <router-view />
        <template #footer>
            <Copyright author="Lenik" org="boDz Inventions" />
        </template>
    </ModelC>
</template>

<style lang="scss" scoped>
.image-grid {
    position: absolute;
    top: 0;
    left: 0;
    z-index: -1;
    opacity: 10%;
    filter: contrast(80%) brightness(130%);
}
</style>
