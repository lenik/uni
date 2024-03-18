<script lang="ts">
import "./table-diagonal.scss";

import { onMounted, ref } from "vue";

import Instant from "../../src/lang/time/Instant";
import LocalDateTime from '../../src/lang/time/LocalDateTime';
import LocalDate from '../../src/lang/time/LocalDate';
import LocalTime from '../../src/lang/time/LocalTime';
import OffsetDateTime from '../../src/lang/time/OffsetDateTime';
import OffsetTime from '../../src/lang/time/OffsetTime';
import ZonedDateTime from '../../src/lang/time/ZonedDateTime';
import formats from '../../src/lang/time/formats';

export const title = "Table of date formats";

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

let fmtgroups = {
    iso: {
        ISO_LOCAL_DATE_TIME: formats.ISO_LOCAL_DATE_TIME,
        ISO_LOCAL_DATE: formats.ISO_LOCAL_DATE,
        ISO_LOCAL_TIME: formats.ISO_LOCAL_TIME,
        ISO_OFFSET_DATE_TIME: formats.ISO_OFFSET_DATE_TIME,
        ISO_OFFSET_TIME: formats.ISO_OFFSET_TIME,
        ISO_ZONED_DATE_TIME: formats.ISO_ZONED_DATE_TIME,
    },

    ui: {
        UI_DATE: formats.UI_DATE,
        UI_TIME: formats.UI_TIME,
        UI_DATE_TIME: formats.UI_DATE_TIME,
        UI_OFFSET_DATE_TIME: formats.UI_OFFSET_DATE_TIME,
        UI_OFFSET_TIME: formats.UI_OFFSET_TIME,
        UI_ZONED_DATE_TIME: formats.UI_ZONED_DATE_TIME,
    },

    fragments: {
        YYYY_MM_DD: formats.YYYY_MM_DD,
        YY_MM_DD: formats.YY_MM_DD,
        YYYYMMDD: formats.YYYYMMDD,
        YYMMDD: formats.YYMMDD,
        HH_MM_SS: formats.HH_MM_SS,
        HHMMSS: formats.HHMMSS,
        HH_MM_SS_Z: formats.HH_MM_SS_Z,
        HHMMSS_Z: formats.HHMMSS_Z,
    },

}
let dates = {
    Instant: Instant.now(),
    LocalDateTime: LocalDateTime.now(),
    LocalDate: LocalDate.now(),
    LocalTime: LocalTime.now(),
    OffsetDateTime: OffsetDateTime.now(),
    OffsetTime: OffsetTime.now(),
    ZonedDateTime: ZonedDateTime.now(),
};

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="fmtgroup" v-for="(formats, i) in fmtgroups" :key="i">
        <h3>Section: {{ i }} </h3>
        <table cellspacing="0">
            <thead>
                <tr>
                    <th class="corner diagonalFalling">Date <br> Format</th>
                    <th>toString()</th>
                    <th v-for="(f, name) in formats" :key="i"> {{ name }} </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(d, k) in dates" :key="k">
                    <th>{{ k }}</th>
                    <td> {{ d.toString() }} </td>
                    <td v-for="(f, name) in formats" :key="name"> {{ d.format(f) }} </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}

table {
    --line-color: gray;

    font-size: 85%;

    tr {

        &:last-child {

            th,
            td {
                border-bottom: solid 1px gray;
            }
        }

        &:nth-child(even) {
            background-color: #fef;
        }

        th,
        td {
            border-top: solid 1px gray;
            border-left: solid 1px gray;

            padding: .2em .5em;
            text-align: center;

            &:last-child {
                border-right: solid 1px gray;
            }
        }

    }


    th.corner {}

    th {
        font-weight: 500;
    }

    td {
        font-weight: 300;
        white-space: nowrap;
    }
}
</style>
