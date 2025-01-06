<script lang="ts">
const title = "component Clock";

import { computed, onMounted, ref } from "vue";

import type { ZoneId } from "skel01-core/src/lang/time/MomentWapper";
import Instant from "skel01-core/src/lang/time/Instant";
import LocalDateTime from "skel01-core/src/lang/time/LocalDateTime";
import ZonedDateTime from "skel01-core/src/lang/time/ZonedDateTime";
import OffsetDateTime from "skel01-core/src/lang/time/OffsetDateTime";
import moment from "moment-timezone";

export interface Props {
    offset?: Instant | number | 'dayStart'
    timeZone?: ZoneId
    align?: 'v' | 'h'
    showDate?: boolean
    showTime?: boolean
    showSecond?: boolean
    showMs?: boolean
    datePadding?: boolean
    hourPadding?: boolean
    showTimeZone?: boolean
    showTitle?: boolean | 'auto'
    showHourArm?: boolean
    showMinuteArm?: boolean
    showSecondArm?: boolean
    logo?: string
    logo2?: string
    theme?: string
    hue?: number
}
</script>

<script setup lang="ts">
import Icon from "skel01-core/src/ui/Icon.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    // offset: -60, // 'dayStart',
    // timeZone: 'Asia/Tokyo',
    align: 'v',
    showDate: false,
    showTime: true,
    showSecond: false,
    showMs: false,
    showTimeZone: false,
    showTitle: 'auto',
    datePadding: false,
    hourPadding: true,
    showHourArm: true,
    showMinuteArm: true,
    showSecondArm: true,
    logo: 'Lenik',
    logo2: 'Reinvent',
    hue: 195,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const isVert = computed(() => props.align == 'v');
const flexDir = computed(() => isVert.value ? 'column' : 'row');
const textAnchor = computed(() => isVert.value ? 'middle' : 'left');
const titleSize = computed(() => isVert.value ? 13 : 18);
const textSize = computed(() => isVert.value ? 12 : 18);

const highlightFill = computed(() => props.hue && compileHsl(props.hue, '65%', '55%'));

const styleClasses = computed(() => {
    let map: any = {
        'align-h': props.align == 'h',
        'align-v': props.align == 'v',
    };
    if (props.theme != null)
        map['theme-' + props.theme] = true;
    return map;
});

// app state

const defaultStart = Instant.now();

const sysNow = ref<Instant>(Instant.now());
const localSeconds = computed(() => {
    let epoch0 = 0;
    if (props.offset != null)
        switch (props.offset) {
            case 'dayStart':
                let dayStart = LocalDateTime.ofEpochDay(Math.floor(defaultStart.epochDay));
                epoch0 = dayStart.epochSecond;
                break;

            default:
                if (typeof props.offset == 'number') // tz/minutes
                    epoch0 = -props.offset * 60;
                else
                    epoch0 = props.offset.epochSecond;
        }
    else if (props.timeZone != null) {

    }
    let val = sysNow.value.epochSecond - epoch0;
    return val;
});
const now = computed(() => {
    let tz = props.timeZone;
    if (tz == null)
        tz = moment.tz.guess();
    let zdt = ZonedDateTime.ofEpochSecond(localSeconds.value, tz);
    return zdt;
});

const year = computed(() => now.value.year);
const month = computed(() => props.datePadding ? right('00' + now.value.month1, 2) : now.value.month1);
const day = computed(() => props.datePadding ? right('00' + now.value.date, 2) : now.value.date);

const hour = computed(() => props.hourPadding ? right('00' + now.value.hour, 2) : now.value.hour);
const minute = computed(() => right('00' + now.value.minute, 2));
const second = computed(() => right('00' + now.value.second, 2));
const ms = computed(() => (now.value.millisecond + '000').substring(0, 3));

const tzTime = computed(() => '' + now.value.zoneOffset);
const tzTitle = computed(() => {
    let s = now.value.tz;
    if (s != null) {
        let last = s.lastIndexOf('/');
        s = s.substring(last + 1);
        s = s.replace('_', ' ');
    }
    return s;
});
const _showTitle = computed(() => typeof props.showTitle == 'boolean'
    ? props.showTitle
    // : (props.timeZone != null && !props.showTimeZone)
    : props.timeZone != null
);

const secondShaker = computed(() => {
    if (now.value.millisecond < 40)
        return 4;
    else if (now.value.millisecond < 60)
        return -2;
    else
        return 0;
});
const secondArm = computed(() => now.value.second / 60 * 360 - 90 + secondShaker.value);

const minuteShaker = computed(() => now.value.second == 0 ? secondShaker.value : 0);
const minuteArm = computed(() => now.value.minute / 60 * 360 - 90 + minuteShaker.value);

const hourShaker = computed(() => (now.value.minute == 0 && now.value.second == 0) ? secondShaker.value : 0);
const hourArm = computed(() => (now.value.hour + now.value.minute / 60) / 12 * 360 - 90 + hourShaker.value);

// DOM references
const rootElement = ref<HTMLElement>();

// methods
defineExpose({ update });

function update() {
}

function right(s: string, n: number) {
    if (s.length > n)
        return s.substring(s.length - n);
    else
        return s;
}

function compileHsl(hue: number | string, sat: number | string, lum: number | string) {
    return 'hsl(' + hue + ', ' + sat + ', ' + lum + ')';
}

function compile(fn: string, ..._args: (number | string)[]) {
    let s = fn + '(';
    let prev = null;
    if (typeof _args[0] == 'string')
        [prev, ..._args] = _args;

    let i = 0;
    for (let a of _args) {
        if (i++ != 0) s += ' ';
        s += a;
    }
    s += ')';
    if (prev != null) s += ' ' + prev;
    return s;
}

function rotate(...args: (number | string)[]) { return compile('rotate', ...args); }
function translate(...args: (number | string)[]) { return compile('translate', ...args); }
function scale(...args: (number | string)[]) { return compile('scale', ...args); }

onMounted(() => {
    setInterval(() => {
        let now = Instant.now();
        sysNow.value = now;
    }, 17);
});
</script>

<template>
    <div class="clock" :class="styleClasses" ref="rootElement">
        <svg class="shape" viewBox="0 0 100 100">
            <g stroke-width=".5" stroke="gray" fill="white">
                <circle class="shadow" cx="50" cy="50" r="47" />
            </g>
            <!-- logo -->
            <g text-anchor="middle" font-size="8" font-family="Chancery Uralic, Schoolbook Uralic, monofur, UnPilgi"
                font-style="italic" :transform="translate(50, 70)">
                <text :transform="translate(0, 0)">{{ logo }}</text>
                <text :transform="translate(0, 8)">{{ logo2 }}</text>
            </g>
            <!-- minute marks -->
            <g stroke="none" fill="#bbb">

                <template v-for="n in 60">
                    <rect width="2.5" height=".7" :transform="//
                        translate(
                            rotate(
                                translate(41.5, -.35),
                                n * 6),
                            50, 50)" v-if="(n % 5 != 0)
                                && (n < 40 || n > 50)" />
                </template>
            </g>
            <!-- hour marks -->
            <g stroke="none" fill="#777">

                <template v-for="n in 12">
                    <rect width="3" height="1.5" :transform="//
                        translate(
                            rotate(
                                translate(41, -0.75),
                                n * 30),
                            50, 50)" v-if="n % 3 != 0" />
                </template>
            </g>
            <!--3 / 6 / 9 / 12 hr -->
            <g stroke="none" class="highlight">

                <template v-for="n in 4">
                    <rect width="9" height="1.5" v-if="n != 3" :transform="//
                        translate(
                            rotate(
                                translate(36, -0.75),
                                n * 90),
                            50, 50)" />
                </template>
                <text text-anchor="middle" letter-spacing="-2" font-size="16"
                    font-family="Roboto Condensed Medium, Nimbus Sans Narrow, Sans"
                    :transform="translate(48, 18)">12</text>
            </g>
            <!-- hour arm -->
            <g class="shadow2" stroke="none" fill="#000" :transform="translate(rotate(hourArm), 50, 50)"
                v-if="showHourArm">
                <circle cx="0" cy="0" r="3" />
                <path d="M 0 -1.5 l22 0 l1.5 1.5 l-1.5 1.5 l-22 0 z" />
            </g>
            <!-- minute arm -->
            <g class="shadow2" stroke="none" fill="#000" :transform="translate(rotate(minuteArm), 50, 50)"
                v-if="showMinuteArm">
                <circle cx="0" cy="0" r="2" />
                <path d="M 0 -.75 l35 0 l0 1.5 l-35 0 z" />
            </g>
            <!-- second arm -->
            <g class="highlight shadow2" stroke="none" :transform="translate(rotate(secondArm), 50, 50)"
                v-if="showSecondArm">
                <circle cx="0" cy="0" r="1" />
                <path d="M 0 -.5 l40 0 l0 1 l-40 0 z" />
            </g>
        </svg>
        <div class="texts">
            <svg class="tz-title" :viewBox="'0 0 100 ' + (titleSize + 7)" v-if="_showTitle">
                <g :font-size="titleSize" font-weight="400"
                    :transform="isVert ? translate(50, titleSize) : translate(0, titleSize)">
                    <text :text-anchor="textAnchor"> {{ tzTitle }} </text>
                </g>
            </svg>
            <svg class="date-time" :viewBox="'0 0 100 ' + (textSize + 3)">
                <g :font-size="textSize" font-weight="300"
                    :transform="isVert ? translate(50, textSize) : translate(0, textSize)">
                    <text :text-anchor="textAnchor">
                        <tspan class="date" v-if="showDate">
                            <tspan class="year">{{ year }}</tspan>
                            <tspan class="month">-{{ month }}</tspan>
                            <tspan class="day">-{{ day }}</tspan>
                        </tspan>
                        <tspan v-if="showDate && showTime">&nbsp;</tspan>
                        <tspan class="time" v-if="showTime">
                            <tspan class="hour">{{ hour }}</tspan>
                            <tspan class="minute">:{{ minute }}</tspan>
                            <tspan class="second" v-if="showSecond">:{{ second }}</tspan>
                            <tspan class="ms" v-if="showSecond && showMs">.{{ ms }}</tspan>
                        </tspan>
                        <tspan class="tz-time" v-if="showTimeZone">&nbsp;{{ tzTime }} </tspan>
                    </text>
                </g>
            </svg>
        </div>
    </div>
</template>

<style scoped lang="scss">
.clock {
    white-space: nowrap;
    display: flex;
    flex-direction: v-bind(flexDir);
    overflow: hidden;
    align-items: center;

    &.align-h svg {
        // height: 100%;
    }

    &.align-v svg {
        // width: 100%;
    }

    svg {
        box-sizing: border-box;
        // border: solid 1px red;

        .shadow {
            -webkit-filter: drop-shadow(2px 2px 1.5px rgba(0, 0, 0, .4));
            filter: drop-shadow(2px 2px 1.5px rgba(0, 0, 0, .4));
        }

        .shadow2 {
            -webkit-filter: drop-shadow(2px 2px 0px rgba(0, 0, 0, .1));
            filter: drop-shadow(2px 2px 0px rgba(0, 0, 0, .1));
        }

        .highlight {
            fill: v-bind(highlightFill);
        }

    }

    .texts {
        display: flex;
        flex-direction: column;
        width: 100%;
        margin-left: .5em;
    }

    .tz-title {
        text-align: center;
        font-size: 150%;
        font-weight: 400;
    }

    .date-time {
        text-align: center;
    }

    .date:after {
        content: " ";
    }

    .month:before {
        content: "-";
    }

    .day:before {
        content: "-";
    }

    .minute:before {
        content: ":";
    }

    .second:before {
        content: ":";
    }

    .ms:before {
        content: ".";
    }

    .tz-time:before {
        content: " ";
    }

    &.theme-cyan .highlight {
        fill: hsl(195, 65%, 55%);
    }

    &.theme-green .highlight {
        fill: hsl(80, 80%, 35%);
    }

    &.theme-pink .highlight {
        fill: hsl(330, 85%, 70%);
    }

    &.theme-purple .highlight {
        fill: hsl(280, 50%, 70%);
    }

    &.theme-red .highlight {
        fill: hsl(0, 80%, 70%);
    }

}
</style>
