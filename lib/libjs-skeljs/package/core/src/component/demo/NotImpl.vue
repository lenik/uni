<script setup lang="ts">

import "../../skel/skel.scss";

interface Props {
}

const props = withDefaults(defineProps<Props>(), {

});

</script>

<template>
    <div class="notimpl full-size"></div>
</template>

<style lang="scss" scoped>
.notimpl {
    background-size: 100% 100%;
    margin: 0;

    &:nth-child(odd) {
        background-image: url(../../../image/notimpl1.jpg);
    }

    &:nth-child(even) {
        background-image: url(../../../image/notimpl2.jpg);
    }

    >* {
        display: none;
    }
}
</style>