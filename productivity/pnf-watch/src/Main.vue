<script lang="ts">
const title = "component Main";

import { computed, onMounted, ref } from "vue";
// import { ipcRenderer} from 'electron';

export interface Props {
}
</script>

<script setup lang="ts">
import Icon from "skel01-core/src/ui/Icon.vue";
import Clock from "./Clock.vue";
import Progress from "./Progress.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

// app state
const hues = ref(Array(40).fill(0).map(() => Math.random() * 360));
const showTitle = ref(true);
const showLabel = ref(false);

const _winTitle = ref('');
const winTitle = computed({
    get: () => _winTitle.value,
    set: (v: string) => {
        _winTitle.value = v;
        window.ipcRenderer.setTitle(v)
    }
});

// DOM references
const rootElement = ref<HTMLElement>();

// methods
defineExpose({ update });

function update() {
}

function random(n: number) {
    return Math.floor(Math.random() * n);
}

onMounted(async () => {
    let title = await window.ipcRenderer.getTitle();
    _winTitle.value = title;
});
</script>

<template>
    <div class="pnf-main" ref="rootElement">
        <div class="top">
            <div class="clocks">
                <Clock :showTitle="true" theme="cyan" />
                <div class="alts">
                    <Clock align="h" :showSecondArm="false" timeZone="Asia/Tokyo" theme="green" />
                    <Clock align="h" :showSecondArm="false" timeZone="America/Los_Angeles" theme="red" />
                </div>
            </div>
            <div class="summary">
                <ul class="inline-block">
                    <li> <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" :hue="hues[0]" title="Task"
                            label='Done' :value="34" :Progress="322" /></li>
                    <li> <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" :hue="hues[1]"
                            title="Slice" label='Done' :value="3" :Progress="50" /></li>
                    <li> <Progress class="pie" :showTitle="showTitle" :showLabel="showLabel" :hue="hues[2]"
                            title="Work Time" label='Done' :value="75" :Progress="480" /></li>
                </ul>
            </div>
            <div class="awards"> Awards... </div>
            <div class="tomatoes">
                <ul class="inline-block">
                    <li v-for="n in 30">
                        <Progress class="pie" :title="'' + n" :showData="false" :hue="hues[n]" :value="random(100)"
                            :scale="100" />
                    </li>
                </ul>
            </div>
        </div>
        <!-- <div class="top2"> Versions: {{ chromeVer }} . </div> -->
        <div class="mid">
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
                <ul>
                    <li v-for="n in 10">
                        <Progress class="pie" :showData="false" :hue="hues[n]" :value="random(100)" />
                        <div> Something very very long to say...</div>
                    </li>
                </ul>
            </div>
        </div>
        <div class="bottom">
        </div>
    </div>
</template>

<style scoped lang="scss">
.pnf-main {
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
    align-items: stretch;
    justify-items: center;

    .clocks {
        display: flex;
        flex-direction: row;
        align-items: center;
        margin-right: .5em;

        >.clock {
            width: 6em;
            margin: .5em;
        }

        .alts .clock {
            height: 4em;
            width: 8em;
        }

    }

    .summary {
        flex: 1;
        border-left: solid 1px gray;
        padding-left: .5em;
        display: flex;
        align-items: center;


        .pie {
            width: 4em;
        }
    }

    .awards {
        border-left: solid 1px gray;
        padding: 0 .5em;
    }

    .tomatoes {
        display: flex;
        align-items: center;
        border-left: solid 1px gray;
        max-width: 22em;

        .pie {
            width: 2.5em;
            margin: 1px;
        }
    }
}

.mid {
    flex: 1;
    display: flex;
    flex-direction: row;
    overflow: hidden;
    border-top: solid 1px gray;
    border-bottom: solid 1px gray;
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
    display: flex;
    max-width: 15em;

    ul {
        list-style: none;
        margin: 0;
        padding: 0;
        // display: flex;
        // flex-direction: column;

        li {
            display: flex;
            flex-direction: row;
            align-items: center;
        }

        .pie {
            width: 2em;
            margin-right: .5em;
        }
    }
}

.bottom {
    display: flex;
    flex-direction: row;

    .current-task {
        flex: 1;
    }

    .datetime2 {
        width: 5em;
    }
}
</style>
