<script setup lang="ts">

defineProps<{
}>();

</script>

<template></template>

<style lang="scss" scoped>
input.smart {
    font-family: inherit;
    font-size: inherit;
    min-width: 1em;
    border: none;
    box-shadow: none;
    color: inherit;
    background: none;

    &::placeholder {
        color: inherit;
        opacity: 0.4;
    }
}

input.smart.ul {
    &:not([readonly]) {
        &:focus {
            outline: none;
            background: #fff;
            border-bottom: solid 1px #69b;
        }

        &:hover {
            border-bottom: solid 1px #69b;
        }
    }
}

input.smart.ul-always {
    border-bottom: solid 1px #69b;

    &:hover {
        background: #fff;
    }
}
</style>