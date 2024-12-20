<script lang="ts">
import { onMounted, ref } from "vue";
import { } from "./types";

export interface Props {
    type: PredefType
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

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <table cellspacing="0">
        <thead>
            <th>FieldName</th>
            <th>Key</th>
            <th>Name</th>
            <th>Item</th>
        </thead>
        <tbody>
            <tr v-for="(a, k) in type.nameMap">
                <td>{{ a.fieldName }}</td>
                <td>{{ a.key }}</td>
                <td>{{ a.name }}</td>
                <td>{{ a }}</td>
            </tr>
        </tbody>
    </table>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}

table {
    border: solid 1px gray;

    th:not(:first-child),
    td:not(:first-child) {
        border-left: solid 1px gray;
    }

    thead {
        background-color: #eef;

        td,
        th {
            border-bottom: solid 1px gray;
        }
    }

    tbody {
        tr:nth-child(even) td {
            background-color: #fee;
        }
    }

}
</style>
