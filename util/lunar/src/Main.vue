<script lang="ts">
import { computed, onMounted, ref } from "vue";
import moment from 'moment-timezone';
import LocalDate from "@skel01/core/src/lang/time/LocalDate";
import LunarDate from "@skel01/core/src/lang/time/LunarDate";

import FieldRow from "@skel01/core/src/ui/FieldRow.vue";
import CmdButton from "@skel01/core/src/ui/CmdButton.vue";

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

const localDate = ref<LocalDate>(new LocalDate({
    year: 1982,
    month: 4 - 1,
    date: 27
}));

// const year = computed({
//     get() { return localDate.value.year },
//     set(val: number) { localDate.value.year = val }
// });

// const month = computed({
//     get() { return localDate.value.month + 1 },
//     set(val: number) { localDate.value.month = val - 1 }
// });

// const dayOfMonth = computed({
//     get() { return localDate.value.date },
//     set(val: number) { localDate.value.date = val }
// });

const lunar = computed(() => {
    let lunarDate = LunarDate.fromMoment(localDate.value.moment);
    lunarDate.onchange = d => localDate.value.moment = d.moment;
    return lunarDate;
});

// methods

defineExpose({ update });

function update() {
}

const matches = computed(() => {
    let birthday = localDate.value;
    let lunarBirthday = lunar.value;
    let v: any[] = [];
    for (let y = 0; y < 120; y++) {
        let theDay = moment({
            year: birthday.year + y,
            month: birthday.month,
            date: birthday.date
        });
        let l = LunarDate.fromMoment(theDay);
        if (l.month == lunarBirthday.month &&
            l.date == lunarBirthday.date) {

            let date = new LocalDate(theDay).format("Y年M月D日");
            let lunar = l.lunar;
            let ls = lunar.toString();
            let age = y;
            v.push(`${date}, 阴历 ${ls}, 年龄 ${age} 周岁`);
        }
    }
    return v;
});

onMounted(() => {
});

console.log("start");

</script>

<template>
    <div class="component-root" ref="rootElement">
        <FieldRow label="预设 (Preset)" icon="far-user">
            <select v-model="localDate.formatted">
                <option value="1982-04-27">雨田</option>
                <option value="1983-02-06">女鬼</option>
            </select>
        </FieldRow>
        <FieldRow label="阳历 (Solar Birthday)" icon="far-sun">
            <input class="year" type="number" v-model="localDate.year">
            <span class='hint'>年</span>
            <input class="month" type="number" v-model="localDate.month1">
            <span class='hint'>月</span>
            <input class="day" type="number" v-model="localDate.date">
            <span class='hint'>日</span>
            <input class="leap" type="checkbox" v-model="localDate.isLeapMonth">
            <span class='hint'>润月 (Leap Month)</span>
            <input type="date" v-model="localDate.isoFormat">
        </FieldRow>
        <FieldRow label="阴历 (Lunar Birthday)" icon="far-moon">
            <input class="year" type="number" v-model="lunar.year">
            <span class='hint'>年</span>
            <input class="month" type="number" v-model="lunar.month">
            <span class='hint'>月</span>
            <input class="day" type="number" v-model="lunar.date">
            <span class='hint'>日</span>
            <input class="leap" type="checkbox" v-model="lunar.isLeapMonth">
            <span class='hint'>润月 (Leap Month)</span>
        </FieldRow>
        <p> 你的一生中, 有哪些日子是阳历生日, 同时又是阴历生日的? 点击:
            <CmdButton label="计算" icon="fa-calculator" />
        </p>
        <ol>
            <li v-for="(d, i) in matches" :key="i"> {{ d }} </li>
        </ol>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}

select {
    font-family: monospace;
    font-weight: 400;
    padding: .2em 1em;
}

input[type=number] {
    border: none;
    border-bottom: dashed 1px gray;
    text-align: center;
    font-family: monospace;
    font-weight: 400;
}

input[type=date] {
    margin-left: 2em;
    // border: none;
    // border-bottom: dashed 1px gray;
    padding: .2em 1em;
}

input,
select {
    color: hsl(330, 50%, 50%);
}

.year {
    width: 6em;
}

.month {
    width: 4em;
}

.day {
    width: 4em;
}

.hint {
    color: gray;
}

.field-row {
    margin: .5em 0;
}
</style>
