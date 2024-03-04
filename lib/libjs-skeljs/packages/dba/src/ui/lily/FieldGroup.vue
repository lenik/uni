<script lang="ts">
import { onMounted, ref } from "vue";
import { } from "../../../../core/src/ui/types";
import IEntityType from "../../net/bodz/lily/entity/IEntityType";

export interface Props {
    type: IEntityType
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

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="field-group">
        <div class="header">
            <Icon :name="type.icon" v-if="type.icon != null" />
            <span class="label" v-if="type.label != null">{{ type.label }}</span>
            <span class="fqcn" v-else>{{ type.name }}</span>
        </div>
        <div class="body">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.field-group {
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
}

.body {
    border-radius: 0 0 var(--radius) var(--radius);
    border: solid 1px hsl(80, 30%, 60%);
    background-color: hsl(80, 60%, 98%);
    padding: 0 .3em;
}
</style>
