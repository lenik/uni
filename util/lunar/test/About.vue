<script lang="ts">
import { onMounted, ref } from "vue";
import { } from "./types";

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
        <p>这是一个计算生日的小程序。</p>
        <p>使用方法：</p>
        <el-steps direction="vertical" space="5em" :active="active">
            <el-step title="Step 1" @click="active = 0">
                <template #title>输入生日 </template>
                <template #description>
                    <p>可以通过阳历或阴历指定出生日期。</p>
                    <p>当用任一种方式（比如阳历/格里高历历）指定日期后，程序将自动计算出 另一种日历法（月历或称农历）表示的日期。</p>
                </template>
            </el-step>
            <el-step title="Step 2" @click="active = 1">
                <template #title>检查 </template>
                <template #description>
                    <p>仔细观察输入的日期，确保阳历和阴历的生日是您的出生日期。</p>
                    <p>当你用农历日历法来输入日期时，也许会存在润月的情况。</p>
                    <p> 严格来说，按农历日历法计算生日时，农历润月出生的生日也应在农历润月时才能算作生日。</p>
                </template>
            </el-step>
            <el-step title="Step 3" @click="active = 2">
                <template #title>计算匹配的生日日期 </template>
                <template #description>
                    <p>点击计算按钮</p>
                    <p>系统将从出生日开始起的120年内寻找阳历和阴历同时匹配的所有生日日期（可能不存在），并将它们列出来。</p>
                </template>
            </el-step>
            <el-step title="Step 4" @click="active = 4">
                <template #title>完成或重新来过 </template>
            </el-step>
        </el-steps>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 1em;
    font-weight: 300;
    margin: 0;
    overflow: auto;

    p {
        margin: 0.5em 0;
    }
}
</style>
