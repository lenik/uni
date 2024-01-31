<script setup lang="ts">

import { onMounted, ref } from "vue";
import { } from "../../src/ui/table/types";
import { Person } from "./Person";

import FieldRow from '@skeljs/core/src/ui/FieldRow.vue';
import { getDefaultFieldRowProps } from "../../src/ui/table/defaults";

const model = defineModel<Person>();

interface Props {
}

const props = withDefaults(defineProps<Props>(), {
});

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

// property shortcuts

const property = Person.TYPE.property;

const fieldRowProps = getDefaultFieldRowProps({ labelWidth: '5em' });

const rootElement = ref<HTMLElement>();

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});

</script>

<template>
    <div class="entity-editor person-editor" ref="rootElement" v-if="model != null">
        <FieldRow v-bind="fieldRowProps" :icon="property.label.icon" label="Name">
            <input type="text" v-model="model.label" placeholder="enter text...">
            <template #description> A username is a unique identifier used by individuals to access various online platforms
                or websites. It is typically chosen by the user during the registration process and is often required along
                with a password to log in to an account. </template>
        </FieldRow>
        <FieldRow v-bind="fieldRowProps" :icon="property.gender.icon" label="Gender">
            <select v-model="model.gender">
                <option value="m">Male</option>
                <option value="f">Female</option>
                <option value="x">Other</option>
            </select>
            <template #description> It's important to note that while sex is typically assigned at birth based on physical
                characteristics, gender identity is a deeply personal sense of being male, female, or something else, which
                may or may not align with the sex assigned at birth. </template>
        </FieldRow>
        <FieldRow v-bind="fieldRowProps" :icon="property.birthday.icon" label="Birthday">
            <input type="date" v-model="model.birthday" placeholder="select birthday...">
            <template #description> Age is a term that refers to the length of time a person has been alive or the number of
                years that have passed since their birth. It is often used to determine a person's stage of life, rights,
                responsibilities, and legal obligations. Age can also be used to classify individuals into different groups,
                such as children, teenagers, adults, or seniors. In many countries, age is a significant factor in various
                aspects of life, including education, employment, marriage, voting rights, driving privileges, and
                retirement. It is important to note that age can vary greatly among individuals and is influenced by factors
                such as culture, genetics, and lifestyle choices. </template>
        </FieldRow>
        <hr>
        <FieldRow v-bind="fieldRowProps" :icon="property.father.icon" label="Father">
            <input type="hidden" v-model="model.fatherId">
            <div class="text">
                <span class="id">{{ model.fatherId }} </span>
                <span class="label" v-if="model.father != null">{{ model.father.label }}</span>
            </div>
            <div class="btn">...</div>
            <template #description>Father is father</template>
        </FieldRow>
    </div>
</template>

<style scoped lang="scss">
.entity-editor {
    padding: 0;
}
</style>
