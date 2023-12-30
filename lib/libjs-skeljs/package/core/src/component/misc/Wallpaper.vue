<script setup lang="ts">

interface Props {
    src: string
    opacity?: number
}

const props = withDefaults(defineProps<Props>(), {
    opacity: 0.5
});

</script>

<template>
    <img class="wallpaper" :src="src" :style="{ opacity }">
</template>

<style lang="scss" scoped>
img.wallpaper {
    position: fixed;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: -1000;
}
</style>