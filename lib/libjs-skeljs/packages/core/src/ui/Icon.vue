<script lang="ts">
import 'font-awesome/css/font-awesome.css';
import 'velbit-fontawesome-pro/css/all.css';

import { computed, onMounted, ref } from "vue";
import Link from "./Link.vue";

export interface Props {
    name: string
    href?: string
    spin?: boolean
    invert?: boolean
    hFlip?: boolean
    vFlip?: boolean
    color?: string
    border?: 'none' | 'circle' | 'box'
}
</script>

<script setup lang="ts">
const props = withDefaults(defineProps<Props>(), {
    spin: false,
    invert: false,
    hFlip: false,
    vFlip: false,
    border: 'none',
});

const type = computed<string>(() => {
    const name = props.name;
    const dash = name.indexOf('-');
    const prefix = dash == -1 ? undefined : name.substring(0, dash);

    switch (prefix) {
        case "fa":
        case "fab":
        case "far":
        case "fas":
            return 'fa';
    }

    return 'unknown';
});

const faClassList = computed(() => {
    let name = props.name;
    const dash = name.indexOf('-');
    const prefix = dash == -1 ? undefined : name.substring(0, dash);
    if (dash != -1)
        name = name.substring(dash + 1);

    return [prefix, 'fa-' + name];
});

onMounted(() => {
});

defineExpose({
});
</script>

<template>
    <Link :href="href" class="icon">
    <i :class="faClassList" v-if="type == 'fa'"></i>
    <i class="unknown" :title="'unknown icon ' + name" v-if="type == 'unknown'">?</i>
    </Link>
</template>

<style scoped lang="scss">
.component {}
</style>
