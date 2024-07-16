<script setup lang="ts">
import { computed } from "vue";

import Icon from "../Icon.vue";

interface Props {
    to: string
    label: string
}

const props = withDefaults(defineProps<Props>(), {
});

const header = computed(() => {
    let colon = props.label.indexOf(':');
    if (colon == -1)
        return '';
    else
        return props.label.substring(0, colon + 1);
});

const main = computed(() => {
    let colon = props.label.indexOf(':');
    if (colon == -1)
        return props.label;
    else
        return props.label.substring(colon + 1);
});

</script>

<template>
    <router-link :to="to">
        <span class="hdr">{{ header }}</span>
        <b>{{ main }}</b>
    </router-link>
</template>

<style lang="scss" scoped>
.hdr {
    color: gray;
    letter-spacing: -0.05em;
}

b {
    font-weight: 400;
}
</style>