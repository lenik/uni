<script setup lang="ts">

import { onMounted, ref } from "vue";
import { } from "./types";

import FieldRow from '@skeljs/core/src/ui/FieldRow.vue';

interface Person {
    name: string
    gender: string
    birthday: string
    interests: any
    hates: any
}

const model = defineModel<Person>();

interface Props {
}

const props = withDefaults(defineProps<Props>(), {
});

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement>();
onMounted(() => {
});

function update() {
}

defineExpose({ update });

const fieldRowProps: any = {
    tagName: 'li',
    align: 'top',
    alignLabel: 'left',
    labelWidth: '5em',
    description: '...',
    // required: true,
    // watch: false,
};
const fieldTrProps: any = {
    ...fieldRowProps,
    tagName: 'tr',
};

</script>

<template>
    <div class="entity-editor person-editor" ref="rootElement">
        <FieldRow v-bind="fieldRowProps" icon="fa-user" label="Name" v-if="model != null">
            <input type="text" v-model="model.label" placeholder="enter text...">
            <template #description> A username is a unique identifier used by individuals to access various online platforms
                or websites. It is typically chosen by the user during the registration process and is often required along
                with a password to log in to an account. </template>
        </FieldRow>
        <FieldRow v-bind="fieldRowProps" icon="fa-venus-mars" label="Gender" v-if="model != null">
            <select v-model="model.gender">
                <option value="m">Male</option>
                <option value="f">Female</option>
                <option value="x">Other</option>
            </select>
            <template #description> It's important to note that while sex is typically assigned at birth based on physical
                characteristics, gender identity is a deeply personal sense of being male, female, or something else, which
                may or may not align with the sex assigned at birth. </template>
        </FieldRow>
        <FieldRow v-bind="fieldRowProps" icon="fab-pagelines" label="Birthday" v-if="model != null">
            <input type="date" v-model="model.birthday" placeholder="select birthday...">
            <template #description> Age is a term that refers to the length of time a person has been alive or the number of
                years that have passed since their birth. It is often used to determine a person's stage of life, rights,
                responsibilities, and legal obligations. Age can also be used to classify individuals into different groups,
                such as children, teenagers, adults, or seniors. In many countries, age is a significant factor in various
                aspects of life, including education, employment, marriage, voting rights, driving privileges, and
                retirement. It is important to note that age can vary greatly among individuals and is influenced by factors
                such as culture, genetics, and lifestyle choices. </template>
        </FieldRow>
    </div>
</template>

<style scoped lang="scss">
.entity-editor {
    padding: 0;
}
</style>
