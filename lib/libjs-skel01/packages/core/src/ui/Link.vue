<script lang="ts">
import { computed, onMounted, ref } from "vue";
import { } from "./types";

export interface Props {
    href?: string
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const tagName = computed(() => {
    let h = props.href;
    if (h == null || h.length == 0)
        return 'span';

    let colon = h.indexOf(':');
    if (colon != -1)
        switch (h.substring(0, colon)) {
            case 'route-to':
                return 'router-link';
        }
    return 'a';
});

const routerLinkTo = computed(() => {
    let h = props.href;
    if (h != null) {
        let colon = h.indexOf(':');
        if (colon != -1)
            switch (h.substring(0, colon)) {
                case 'route-to':
                    return h.substring(colon + 1).trim();
            }
    }
    return null;
});
// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <component :is="tagName" :to="routerLinkTo" v-bind="$attrs">
        <slot></slot>
    </component>
</template>

<style scoped lang="scss"></style>
