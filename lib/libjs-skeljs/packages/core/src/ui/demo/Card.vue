<script lang="ts">
import { computed, onMounted, ref } from "vue";

import Icon from '../Icon.vue';
import FieldRow from '../FieldRow.vue';

export interface Props {
    label: string
    icon?: string
    href?: string
    version?: string
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

const rootElement = ref<HTMLElement>();
const fieldprops = computed(() => ({
    labelWidth: '6em',
}));

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="card">
        <div class="title">
            <Icon :name="icon" v-if="icon != null" />
            <span class="label"> {{ label }}</span>
        </div>
        <div class="status">
            <FieldRow v-bind="fieldprops" label="Hosted on:" icon="fab-servicestack"> GitHub </FieldRow>
            <FieldRow v-bind="fieldprops" label="Version:" icon="far-microchip" v-if="version != null"> #{{ version }}
            </FieldRow>
            <FieldRow v-bind="fieldprops" label="Checkout:" icon="far-link" v-if="href != null">
                <a :href="href"> Click to visit </a>
            </FieldRow>
        </div>
        <div class="description">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.card {
    background-color: hsl(60, 30%, 90%);
    margin: 0 .5em;
    display: flex;
    flex-direction: column;
}

.title {
    // text-align: center;
    font-family: monospace;
    font-size: 120%;
    background-color: hsl(180, 30%, 90%);
    padding: .3em 1em;
    border-bottom: solid 1px #333;
    display: flex;
    flex-direction: row;
    align-items: center;
    white-space: nowrap;

    .icon {
        margin-right: 1rem;
    }
}


.description {
    padding: 0 .5em;
    font-weight: 300;
    flex: 1;
}

.status {
    background-color: hsl(190, 10%, 90%);

    padding: .2em 1em;
    font-size: 80%;
    font-weight: 200;
    border-bottom: solid 1px #ccc;

    display: flex;
    flex-direction: column;

    .icon {
        display: inline-block;
        width: 1.5rem;
        text-align: center;
    }

    a {

        &:hover {
            background-color: hsl(0, 50%, 80%);
        }
    }
}
</style>
