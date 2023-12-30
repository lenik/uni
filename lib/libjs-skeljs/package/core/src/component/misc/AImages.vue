<script setup lang="ts">

interface Props {
}

const props = withDefaults(defineProps<Props>(), {
});

</script>

<template></template>

<style lang="scss" scoped>
.a-images>li {
    margin: .5em;
    border: solid 1px gray;
    text-align: center;

    img {
        max-width: 4em;
        max-height: 4em;
        object-fit: contain;
    }

    .caption {
        word-break: break-all;
        max-width: 12em;
        font-size: 80%;
    }
}
</style>