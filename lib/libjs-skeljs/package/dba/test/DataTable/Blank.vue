<script setup lang="ts">

import { ref, watch } from 'vue';

interface Props {
}

const model = defineModel();
const props = defineProps<Props>();
const emit = defineEmits<{
}>();

const div = ref(null);

</script>

<template>
    <div :ref="div">
    </div>
</template>

<style lang="scss" scoped>
</style>