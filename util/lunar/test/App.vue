<script setup lang="ts">

import ModelC from '@skeljs/core/src/ui/layout/ModelC.vue';
import SiteBar from '@skeljs/core/src/ui/menu/SiteBar.vue';
import ProjectInfo from '@skeljs/core/src/ui/demo/ProjectInfo.vue';
import Copyright from '@skeljs/core/src/ui/demo/Copyright.vue';
import Main from './Main.vue';
import ImageGrid from './ImageGrid.vue';

const stat = {
    views: 0,
    stars: 0,
    upvotes: 0,
    downvotes: 0,
};

const images = [
    "img/fotor-ai-2024020512818.jpg",
    "img/fotor-ai-2024020512858_10.jpg",
    "img/fotor-ai-2024020512858_12.jpg",
    "img/fotor-ai-2024020512858_13.jpg",
    "img/fotor-ai-2024020512858_15.jpg",
    "img/fotor-ai-2024020512858_16.jpg",
    "img/fotor-ai-2024020512858_17.jpg",
    "img/fotor-ai-2024020512858_18.jpg",
    "img/fotor-ai-2024020512858_1.jpg",
    "img/fotor-ai-2024020512858_2.jpg",
    "img/fotor-ai-2024020512858_3.jpg",
    "img/fotor-ai-2024020512858_4.jpg",
    "img/fotor-ai-2024020512858_5.jpg",
    "img/fotor-ai-2024020512858_6.jpg",
    "img/fotor-ai-2024020512858_7.jpg",
    "img/fotor-ai-2024020512858_9.jpg",
    "img/fotor-ai-202402051287.jpg",
];

</script>

<template>
    <ModelC>
        <template #site-bar>
            <SiteBar />
        </template>
        <template #project-info>
            <ProjectInfo :stat="stat" label="Lunar Birthday"
                description="在一些亚洲国家，特别是中国、韩国和越南等地，人们更加重视传统文化和习俗。阴历生日在这些文化中被认为是更为重要和吉利的。选择阴历生日可以与传统节日和庆祝活动相结合，增加庆祝的乐趣和意义。">
                <ImageGrid :srcs="images" :columns="15" :randomCount="100" />
            </ProjectInfo>
        </template>
        <Main />
        <template #footer>
            <Copyright />
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
