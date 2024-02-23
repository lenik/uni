<script lang="ts">
import { onMounted, ref } from "vue";
import { } from "../../../../core/src/ui/types";
import { IEntityType } from "../../net/bodz/lily/entity";

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
    <div class="header">
        <Icon :name="type.icon" v-if="type.icon != null" />
        <span class="label" v-if="type.label != null">{{ type.label }}</span>
        <span class="fqcn" v-else>{{ type.name }}</span>
        <hr>
    </div>
    <slot></slot>
</template>

<style scoped lang="scss">
.field-group {
    padding: 0;
}
</style>
