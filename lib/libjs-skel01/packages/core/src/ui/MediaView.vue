<script lang="ts">
const title = "component MediaView";

import { computed, onMounted, ref } from "vue";
import { contentTypeOfExtension } from "../util/mime";

export interface Props {
    src: string
}
</script>

<script setup lang="ts">
import Icon from "./Icon.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts
const extension = computed(() => {
    let path = props.src;
    let lastSlash = path.lastIndexOf("/");
    let base = lastSlash == -1 ? path : path.substring(lastSlash + 1);
    let lastDot = base.lastIndexOf(".");
    let ext = lastDot == -1 ? base : base.substring(lastDot + 1);
    return ext;
});

const contentType = computed(() => {
    return contentTypeOfExtension(extension.value);
});

const mainType = computed(() => {
    let type = contentType.value;
    let slash = type.indexOf('/');
    return slash == -1 ? type : type.substring(0, slash);
});

// app state
const playing = ref(true);

// DOM references
const videoElement = ref<HTMLElement>();


// methods
defineExpose({ update });

function pauseResume(e: Event) {
    let video = e.target as HTMLVideoElement;
    if (playing.value)
        video.pause();
    else
        video.play();
    playing.value = !playing.value;
}

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="media-view">
        <img class="content" v-if="mainType == 'image'" :src="src">
        <video class="content" v-else-if="mainType == 'video'" ref="videoElement" autoplay loop muted
            @click="pauseResume">
            <source :src="src" :type="contentType">
            <div> Your browser does not support the video tag. </div>
        </video>
        <div class="content unknown" v-else> extension {{ extension }}, content type {{ contentType }}. </div>
    </div>
</template>

<style scoped lang="scss">
.media-view {
    display: inline-block;
    // display: flex;
    // flex-direction: row;

    overflow: hidden;
    box-sizing: border-box;
    // border: solid 1px red;

    .content {
        // flex: 1;

        width: 100%;
        height: 100%;
        aspect-ratio: 1;
        object-fit: contain;

        box-sizing: border-box;
        // border: dashed 3px blue;
        // outline: 5px dotted red;
    }
}
</style>
