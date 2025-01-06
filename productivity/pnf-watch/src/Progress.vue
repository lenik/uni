<script lang="ts">
const title = "component Pie";

import { computed, onMounted, ref } from "vue";

export interface Props {
    cutout?: number | string;
    outlabels?: boolean | string;
    radius?: number | string;
    rotation?: number;
    circumference?: number;
    animation?: boolean;
    title?: string;
    showTitle?: boolean | string;
    label?: string;
    showLabel?: boolean | string;
    showData?: boolean | string;
    data?: any;
    scale?: number;
    offset?: number;
    value?: number;
    color?: string;
    hue?: number;
    sat?: number;
    lum?: number;
    color0?: string;
    hue0?: number;
    sat0?: number;
    lum0?: number;
}
</script>

<script setup lang="ts">
import { Doughnut } from 'vue-chartjs'
import {
    Chart as ChartJS,
    Title, Tooltip, Legend, BarElement, CategoryScale,
    LinearScale,
    ArcElement
} from 'chart.js'
import annotationPlugin from 'chartjs-plugin-annotation';
import datalabelsPlugin from 'chartjs-plugin-datalabels';
// import outlabelsPlugin from 'chartjs-plugin-piechart-outlabels';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement);
ChartJS.register(annotationPlugin);
ChartJS.register(datalabelsPlugin);
// ChartJS.register(outlabelsPlugin);

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    cutout: '50%',
    radius: '100%',
    rotation: 0,
    circumference: 360,
    animation: true,
    // title: 'notitle',
    // label: 'done',
    showTitle: undefined,
    showLabel: undefined,
    showData: true,
    scale: 100,
    offset: 0,
    hue: 120,
    sat: 0.5,
    lum: 0.4,
    hue0: 0,
    sat0: 0,
    lum0: 0.92,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const outlabels = computed(() => toBool(props.outlabels, props.label != null));

const color = computed(() => props.color != null ? props.color
    : hslStr(props.hue, props.sat, props.lum));

const color0 = computed(() => props.color0 != null ? props.color0
    : hslStr(props.hue0, props.sat0, props.lum0));

// app state

const options = computed(() => {
    let plugins: any = {
        legend: false,
    };

    if (toBool(props.showTitle, props.title != null)) {
        if (props.cutout != 0)
            plugins.annotation = {
                annotations: {
                    dLabel: {
                        type: 'doughnutLabel',
                        content: props.title,
                        // content: ({ chart }) => ['Total', chart.getDatasetMeta(0).total, 'last 7 months'],
                        // font: [{ size: 60 }, { size: 50 }, { size: 30 }],
                        // color: ['black', 'red', 'grey']
                    }
                }
            };
        else
            plugins.title = {
                display: true,
                text: props.title,
            };
    }

    if (toBool(props.showLabel, props.label != null)) {
        if (outlabels.value)
            plugins.outlabels = {
                text: '%l %p',
                color: 'white',
                stretch: 35,
                font: {
                    resizable: true,
                    // minSize: 12,
                    // maxSize: 18,
                },
            };

        plugins.datalabels = {
            display: true,
            color: 'blue',
            labels: {
                name: {
                    color: 'hsl(300, 60%, 30%)',
                    // font: { size: '15' },
                    formatter: (val: any, ctx: any) => ctx.chart.data.labels[ctx.dataIndex],
                },
            }
        };
    }

    if (!toBool(props.showData, true)) {
        plugins.datalabels = {
            display: false
        };
    }

    let options = {
        cutout: props.cutout,
        radius: props.radius,
        rotation: props.rotation,
        circumference: props.circumference,
        'animation.animateRotate': props.animation,
        plugins,
    };
    return options;
});

const dataComp = computed(() => {
    let dataset1: any = {
        data: [
            nz(props.value) - nz(props.offset),
            nz(props.scale) - (nz(props.value) - nz(props.offset))
        ],
        datalabels: {} as any,
    };
    dataset1.backgroundColor = [
        color.value,
        color0.value
    ];
    dataset1.datalabels = {
    };
    let a: any = {
        datasets: [dataset1]
    };
    if (props.label != null)
        a.labels = [props.label, ''];
    return a;
});

const data = computed(() => props.data != null ? props.data
    : dataComp.value);

// DOM references
const rootElement = ref<HTMLElement>();

// methods
defineExpose({ update });

function toBool(val: boolean | string | number | null | undefined, defaultVal: boolean = false): boolean {
    switch (val) {
        case true:
        case 'true':
        case 'yes':
        case 'on':
        case 1:
        case '1':
            return true;

        case false:
        case 'false':
        case 'no':
        case 'off':
        case 0:
        case '0':
            return false;

        case null:
        case undefined:
            return defaultVal;

        default:
            return false;
    }
}
function nz(val?: number): number {
    return val == undefined ? 0 : val;
}

function perc(val: number, p = 100): string {
    return Math.round(val * 100 * p) / p + '%';
}

function hslStr(h: number, s: number, l: number) {
    let sp = perc(s);
    let lp = perc(l);
    let str = `hsl(${h}, ${sp}, ${lp})`;
    return str;
}

function update() {
}

onMounted(() => {
});
</script>

<template>
    <span class="pie">
        <Doughnut :options="options" :data="data" />
    </span>
</template>

<style scoped lang="scss">
.pie {
    display: inline-block;
    padding: 0;
}
</style>
