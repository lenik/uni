<script lang="ts">
import { computed, provide, ref } from 'vue';

export const title = 'field row demo';

const fieldRowProps: any = {
    tagName: 'div',
    align: 'top',
    alignLabel: 'left',
    // labelWidth: '5em',
    // description: '...',
    // required: true,
    // watch: false,
};
</script>

<script setup lang="ts">
import FieldRow, { FIELD_ROW_PROPS } from '../../src/ui/FieldRow.vue';

provide(FIELD_ROW_PROPS, fieldRowProps);

const userName = ref<string>('Tom');
const password = ref<string>('what 8the');
const age = ref<number>(18);

function validateUsername(s: string) {
    if (s.length < 3)
        throw "user name must be at least 3 chars";

    if (s.length > 10)
        throw "user name must be no longer than 10 chars";

    if (!s.match(/^[a-z]/i))
        throw "user name must be starts with letter";

    if (s.match(/\p{P}$/u))
        throw "user name can't be end with puncutation mark";

    if (s.match(/\s/))
        throw "space isn't allowed in user name";
}

function validatePassword(s: string) {
    if (s.length < 8)
        throw "length < 8";

    if (!s.match(/[0-9]/))
        throw "password must contains digit";

    if (!s.match(/[a-z]/i))
        throw "password must contains letter";
}

function validateAge(age: number) {
    if (age < 10)
        throw "you too young, less than 10 years";

    if (age > 100)
        throw "you too old, more than 100 years";
}

const fieldTrProps: any = {
    ...fieldRowProps,
    tagName: 'tr',
};

const valids: any = ref({});

</script>

<template>
    <h2> FieldRow (div) </h2>
    <FieldRow icon="fa-user" label="User Name" v-model="userName" :validator="validateUsername"
        v-model:valid="valids.user">
        <input type="text" v-model="userName" placeholder="enter text...">

        <template #description> A username is a unique identifier used by individuals to access various online platforms
            or websites. It is typically chosen by the user during the registration process and is often required along
            with a password to log in to an account. </template>
    </FieldRow>
    <FieldRow icon="fa-key" label="Password" v-model="password" :validator="validatePassword"
        v-model:valid="valids.password">
        <input type="text" v-model="password" placeholder="enter password...">

        <template #description> A password is a secret combination of characters that is used to prove one's identity or
            gain access to a computer system, online account, or electronic device. It serves as a security measure to
            ensure that only authorized individuals can access sensitive information or perform certain actions.
        </template>
    </FieldRow>
    <FieldRow icon="fa-key" label="Age" v-model="age" :validator="validateAge" v-model:valid="valids.age">
        <input type="number" v-model="age" placeholder="choose your age">

        <template #description> Age is a term that generally refers to the length of time a person has been alive since
            their birth. It is commonly used to indicate the stage of life someone is in and is often measured in years.
            Age can have various implications and is often used as a factor in determining legal rights,
            responsibilities, and privileges. </template>
    </FieldRow>
    <h2> FieldRow (table) </h2>
    <table>
        <thead>
            <tr>
                <th>Icon-Label</th>
                <th>Content</th>
            </tr>
        </thead>
        <FieldRow icon="fa-user" label="User Name" v-model="userName" :validator="validateUsername"
            v-model:valid="valids.user">
            <input type="text" v-model="userName" placeholder="enter text...">

            <template #description> A username is a unique identifier used by individuals to access various online
                platforms or websites. It is typically chosen by the user during the registration process and is often
                required along with a password to log in to an account. </template>
        </FieldRow>
        <FieldRow icon="fa-key" label="Password" v-model="password" :validator="validatePassword"
            v-model:valid="valids.password">
            <input type="text" v-model="password" placeholder="enter password...">

            <template #description> A password is a secret combination of characters that is used to prove one's
                identity or gain access to a computer system, online account, or electronic device. It serves as a
                security measure to ensure that only authorized individuals can access sensitive information or perform
                certain actions. </template>
        </FieldRow>
        <FieldRow icon="fa-key" label="Age" v-model="age" :validator="validateAge" v-model:valid="valids.age">
            <input type="number" v-model="age" placeholder="choose your age">

            <template #description> Age is a term that generally refers to the length of time a person has been alive
                since their birth. It is commonly used to indicate the stage of life someone is in and is often measured
                in years. Age can have various implications and is often used as a factor in determining legal rights,
                responsibilities, and privileges. </template>
        </FieldRow>
    </table>
    <div> model values: <ul>
            <li>User name: {{ userName }}</li>
            <li>Password: {{ password }}</li>
            <li>Age: {{ age }}</li>
        </ul>
    </div>
    <div> error states: <ul>
            <li v-for="(valid, k) in valids" :key="k"> Key {{ k }}: {{ valid }} </li>
        </ul>
    </div>
</template>

<style lang="scss" scoped>
h2 {
    font-size: large;
    font-weight: 500;
}
</style>
