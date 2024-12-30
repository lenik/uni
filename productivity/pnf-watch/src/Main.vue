<script lang="ts">
const title = "component Main";

import { onMounted, ref } from "vue";

export interface Props {
}
</script>

<script setup lang="ts">
import Icon from "skel01-core/src/ui/Icon.vue";
import Progress from "./Progress.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

// app state
const hues = ref(Array(10).map(() => Math.random() * 360));
const showTitle = ref(true);
const showLabel = ref(false);

// DOM references
const rootElement = ref<HTMLElement>();

// methods
defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <div class="top">
            <div class="date">Date</div>
            <div class="current-progress">Current Bar..32%</div>
            <div class="awards">Awards...</div>
        </div>
        <div class="mid">
            <div class="overall">
                <div class="info">
                    <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" :hue="hues[0]" title="Task"
                        label='Done' :value="34" :Progress="322" />
                    <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" :hue="hues[1]" title="Slice"
                        label='Done' :value="3" :Progress="50" />
                    <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" :hue="hues[2]" title="Work Time"
                        label='Done' :value="75" :Progress="480" />
                </div>
                <div class="timers"> Current Tomato: 3/25 </div>
            </div>
            <div class="logman">
                <ul class="inline-block">
                    <li>Log 1</li>
                    <li>Log 2</li>
                    <li>Log 3</li>
                </ul>
                <div class="to-do">
                    <label for="todo">To do:</label>
                    <input id="todo" type="text">
                </div>
                <div class="done">
                    <label for="done">Done:</label>
                    <input id="done" type="text">
                </div>
            </div>
            <div class="tasks">
                <ul class="inline-block">
                    <li>
                        <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" title="Task1" label="Done"
                            :value="75" />
                    </li>
                    <li>
                        <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" title="Task2" label="Done"
                            :value="50" />
                    </li>
                    <li>
                        <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" title="Task3" label="Done"
                            :value="15" />
                    </li>
                </ul>
            </div>
        </div>
        <div class="bottom">
            <div class="current-task">Current Task:...</div>
            <div class="datetime2">Date Time:</div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    height: 100%;
    border: solid 2px gray;
    box-sizing: border-box;
}

.top {
    display: flex;
    flex-direction: row;

    .date {}

    .current-progress {
        flex: 1;
    }

    .awards {}
}

.mid {
    flex: 1;
    display: flex;
    flex-direction: row;
    overflow: hidden;
    border-top: solid 1px gray;
    border-bottom: solid 1px gray;
}

::v-deep(.pie) {
    width: 10em;
}

.overall {
    display: flex;
    flex-direction: column;

    .info {
        flex: 1;
        overflow: hidden;
        display: inline-block;
    }

    .timers {}
}

.logman {
    flex: 1;
    display: flex;
    flex-direction: column;
    border-left: solid 1px gray;
    border-right: solid 1px gray;
    padding: 4px;

    ul {
        flex: 1;
        display: flex;
        flex-direction: column;
        border-bottom: dashed 1px gray;
    }

    .to-do,
    .done {
        margin: 4px 0 4px 0;
        display: flex;
        flex-direction: row;

        label {
            width: 4em;
        }

        input {
            flex: 1;
            border: none;
            border-bottom: solid 1px gray;
        }
    }
}

.tasks {
    ul {
        display: flex;
        flex-direction: column;
    }
}

.bottom {
    display: flex;
    flex-direction: row;

    .current-task {
        flex: 1;
    }

    .datetime2 {}
}
</style>
