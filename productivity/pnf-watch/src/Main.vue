<script lang="ts">
const title = "component Main";

import { computed, onMounted, ref, watchEffect } from "vue";
import ZonedDateTime from 'skel01-core/src/lang/time/ZonedDateTime';
import LocalDate from 'skel01-core/src/lang/time/LocalDate';

import { Diary, loadDiaries } from "./parser";

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

const dateNow = ref(ZonedDateTime.now());

const _winTitle = ref('');
const winTitle = computed({
    get: () => _winTitle.value,
    set: (v: string) => {
        _winTitle.value = v;
        window.browserWindow.setTitle(v)
    }
});

const baseDir = ref("/mnt/istore/pro/diary");
const dirList = ref<string[]>([]);

const dayMax = ref(10);
const dayCount = ref(3);
const dayCountList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 50];

const dateEnd = computed(() => {
    let date = dateNow.value;
    return LocalDate.of(date.year, date.month, date.dayOfMonth);
});
const dateStart = computed(() => {
    let end = dateEnd.value;
    let startEpoch = end.epochDay - dayCount.value + 1;
    return LocalDate.ofEpochDay(startEpoch);
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

async function chooseDir() {
    let selection = await dialog.showOpenDialog({
        title: 'my title',
        defaultPath: baseDir.value,
        properties: ['openDirectory', 'createDirectory',],
    });
    if (selection != null)
        baseDir.value = selection[0];
}

const diaryData = ref<Diary[]>([]);

const input = ref<string>('');

watchEffect(async () => {
    diaryData.value = await loadDiaries(baseDir.value, dateStart.value, dayCount.value);
});

onMounted(async () => {
    let title = await window.browserWindow.getTitle();
    _winTitle.value = title;

    const fiveMinutes = 5 * 60 * 1000;
    setInterval(() => dateNow.value = ZonedDateTime.now(), fiveMinutes);

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
                    <div class="date-text">
                        <span class="weekday">{{ dateNow.format('ddd') }}</span>
                        <span class="date">{{ dateNow.format('YYYY-MM-DD') }}</span>
                    </div>
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
                <div class="options">
                    <span class="dir" @click="chooseDir()"> {{ baseDir }} </span>
                    <ul class="moons inline-block">
                        <li>
                            <span class="range"> Since {{ dateStart.toString() }} </span>
                        </li>
                        <li v-for="n in dayMax " @click="dayCount = dayMax - n + 1">
                            <Icon class="no" name="far-moon" v-if="n <= dayMax - dayCount" />
                            <Icon class="yes" name="fa-moon" v-else />
                        </li>
                    </ul>
                    <select v-model="dayCount">
                        <option :value="n" v-for="n in dayCountList">{{ n }}</option>
                    </select>
                </div>
                <ul class="logs">

                    <template v-for="diary in diaryData">
                        <li class="day-group">{{ diary.date.toString() }}</li>
                        <li class="log" v-for="log in diary.logs">
                            <div class="hdr">
                                <span class="seq"> {{ log.seq }} </span>
                                <span class="date" v-if="log.date != null"> {{ log.date.format("MM-DD") }} </span>
                                <span class="time"> {{ log.time.format('HH:mm:ss') }} </span>
                                <span class="head"> {{ log.head }} </span>
                            </div>
                            <ul class="lines" v-if="log.lines.length">
                                <li v-for="line in log.lines"> {{ line }} </li>
                            </ul>
                        </li>
                    </template>
                </ul>
                <div class="input">
                    <div class="btn did">
                        <span> Did
                            <Icon name="fas-sign-in-alt" />
                        </span>
                        <div class="kbd">Shift-Cr</div>
                    </div>
                    <input type="text" v-model="input">
                    <div class="btn go">
                        <span>
                            <Icon name="far-sign-out-alt" /> Go
                        </span>
                        <div class="kbd">Ctrl-Cr</div>
                    </div>
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
@use './Main.scss';
</style>
