<script lang="ts">
import { onMounted, ref } from 'vue';

import LilyAdmin from '../../../../../../src/ui/lily/LilyAdmin.vue';

import { Person } from './Person';
import PersonEditor from './PersonEditor.vue';
import PersonChooseDialog from './PersonChooseDialog.vue';

export const title = 'Person Admin';

export interface Props {
}

</script>

<script setup lang="ts">

const props = withDefaults(defineProps<Props>(), {
});

// const admin = ref<InstanceType<typeof LilyAdmin>>();
const defaultPersonChooseDialog = ref<InstanceType<typeof PersonChooseDialog>>();
const type = Person.TYPE;
const selection = ref<any>({});

onMounted(() => {
});

</script>

<template>
    <LilyAdmin ref="admin" :type="type" v-model="selection">
        <template #columns>
            <th data-field="id" class="id">ID</th>
            <th data-field="properties" class="hidden">properties</th>
            <th data-field="label">Name</th>
            <th data-field="description">Description</th>
            <th data-field="gender">Gender</th>
            <th data-type="date" data-field="birthday">Birthday</th>
            <th data-field="father.label">Father</th>
            <th data-field="mother.label">Mother</th>
            <th data-field="ssn">Social ID</th>
            <th data-field="dln">#Driver</th>
        </template>
        <template #preview>
            <PersonEditor class="editor" v-model="selection" />
        </template>
        <template #side-tools> Side Tools</template>
        <template #editor>
            <PersonEditor class="editor" v-model="selection" />
        </template>
    </LilyAdmin>
</template>

<style lang="scss"></style>

<style scoped lang="scss">
.lily-admin {}
</style>