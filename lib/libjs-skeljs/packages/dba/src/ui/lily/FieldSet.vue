<script lang="ts">
import { computed, onMounted, ref } from "vue";

export interface Props {
    name?: string
    label?: string
    icon?: string
}
</script>

<script setup lang="ts">
import Icon from "@skeljs/core/src/ui/Icon.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const simpleName = computed(() => {
    let fqcn = props.name;
    if (fqcn == null) return null;
    let lastDot = fqcn.lastIndexOf('.');
    let name = lastDot == -1 ? fqcn : fqcn.substring(lastDot + 1);
    return name;
});

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="field-set" :data-fqcn="props.name" ref="rootElement">
        <div class="header">
            <Icon :name="icon" v-if="icon != null" />
            <span class="label" v-if="label != null">{{ label }}</span>
            <span class="name" v-else> &lt;&lt; {{ simpleName }} &gt;&gt; </span>
        </div>
        <div class="body">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.field-set {
    margin-left: 1em;
    margin: .5em 0;
    --radius: .3em;
}

.header {
    margin-left: -1em;
    border-radius: var(--radius) var(--radius) 0 0;
    border: solid 1px gray;
    font-size: 75%;
    font-weight: 400;
    background-color: hsl(190, 25%, 65%);
    color: white;
    display: inline-block;
    padding: 0 1em 0 .5em;

    .icon {
        margin-right: .5em;
    }

    .name {
        font-style: italic;
        color: hsl(120, 50%, 90%);
    }
}

.body {
    border-radius: 0 0 var(--radius) var(--radius);
    border: solid 1px hsl(80, 30%, 60%);
    background-color: hsl(80, 60%, 98%);
    padding: 0 .3em;
}
</style>
