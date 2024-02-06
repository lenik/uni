<script lang="ts">
import { onMounted, ref } from "vue";

export interface Props {
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
const active = ref(0);

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <p>这是一个计算特别生日的小程序。 <br> This is a small program that calculates special birthdays.</p>
        <p>How to use:</p>
        <el-steps direction="vertical" space="5em" :active="active">
            <el-step title="Step 1" @click="active = 0">
                <template #title>Specify the birthday </template>
                <template #description>
                    <p>The date of birth can be specified by the solar or lunar calendar.</p>
                    <p> When a date is specified using any method (such as solar calendar/Gregorian calendar), the program
                        will automatically calculate the date represented by another calendar method (lunar calendar). </p>
                </template>
            </el-step>
            <el-step title="Step 2" @click="active = 1">
                <template #title>Confirmation </template>
                <template #description>
                    <p>Verify the date you entered carefully to make sure the solar and lunar dates match your birth date.
                    </p>
                    <p>When you use the lunar calendar method to enter dates, there may be a leap month situation.</p>
                    <p> Strictly speaking, when calculating birthdays according to the lunar calendar method, birthdays born
                        in a leap month of the lunar calendar should also be counted as birthdays in a leap month of the
                        lunar calendar.</p>
                </template>
            </el-step>
            <el-step title="Step 3" @click="active = 2">
                <template #title>Calculate matching birthday dates </template>
                <template #description>
                    <p>Click the Calculate button</p>
                    <p>The program will look for all birthday dates that match both the solar and lunar calendars (which may
                        not exist) within 120 years from the date of birth, and list them.</p>
                </template>
            </el-step>
            <el-step title="Step 4" @click="active = 4">
                <template #title>Finish or start over </template>
            </el-step>
        </el-steps>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 1em;
    margin: 0;
    overflow: auto;

    p {
        margin: 1em 0;
    }

    .el-steps {
        margin-left: 2em;
    }

    ::v-deep() {
        .el-step__title {
            font-weight: 500;
            line-height: inherit;
        }

        .el-step__description {
            font-weight: 300;
            line-height: inherit;
        }
    }
}
</style>
