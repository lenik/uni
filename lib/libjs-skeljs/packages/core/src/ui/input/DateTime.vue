<script lang="ts">
import { computed, onMounted, ref } from "vue";

export interface Props {
    tz?: 'local' | 'zoned' | 'offset'
}
</script>

<script setup lang="ts">
import { loadConfigFromFile } from "vite";
import LocalDate from "../../lang/time/LocalDate";
import LocalDateTime from "../../lang/time/LocalDateTime";
import MomentWrapper from "../../lang/time/MomentWapper";
import OffsetDateTime from "../../lang/time/OffsetDateTime";
import ZonedDateTime from "../../lang/time/ZonedDateTime";
import LocalTime from "../../lang/time/LocalTime";

const model = defineModel<string | undefined | null>();

const props = withDefaults(defineProps<Props>(), {
    tz: 'local',
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();

function parseAny(s: string | undefined | null): MomentWrapper | undefined | null {
    if (s === undefined) return undefined;
    if (s === null) return null;
    switch (props.tz) {
        case 'local':
            return LocalDateTime.parse(s);
        case 'zoned':
            return ZonedDateTime.parse(s);
        case 'offset':
            return OffsetDateTime.parse(s);
    }
    throw new Error("unexpected.");
}

const timeZone = computed({
    get(): string | null | undefined {
        let mw = parseAny(model.value);
        if (mw === undefined) return undefined;
        if (mw === null) return null;
        return mw.tz;
    },
    set(zoneId: string | null | undefined) {
        let mw = parseAny(model.value);
        if (zoneId == null) {
            return;
        }
        if (mw != null)
            mw.tz = zoneId;
    }
});

const localDate = computed({
    get(): string | null | undefined {
        let mw = parseAny(model.value);
        if (mw === undefined) return undefined;
        if (mw === null) return null;
        let localDate = mw.format(LocalDate.ISO_LOCAL_DATE);
        return localDate;
    },
    set(localDateStr: string | null | undefined) {
        let mw = parseAny(model.value);
        if (localDateStr == null) {
            return;
        }
        let datePart = LocalDate.parse(localDateStr);
        if (mw == null)
            mw = datePart;
        else {
            mw.year = datePart.year;
            mw.month = datePart.month;
            mw.dayOfMonth = datePart.dayOfMonth;
        }
    }
});

const localTime = computed({
    get(): string | null | undefined {
        let mw = parseAny(model.value);
        if (mw === undefined) return undefined;
        if (mw === null) return null;
        let localTime = mw.format(LocalTime.ISO_LOCAL_TIME);
        return localTime;
    },
    set(localTimeStr: string | null | undefined) {
        let mw = parseAny(model.value);
        if (localTimeStr == null) {
            return;
        }
        let timePart = LocalTime.parse(localTimeStr);
        if (mw == null)
            mw = timePart;
        else {
            mw.hour = timePart.hour;
            mw.minute = timePart.minute;
            mw.second = timePart.second;
            mw.millisecond = timePart.millisecond;
        }
    }
});

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="DateTime" ref="rootElement">
        <input class="date" type="date" v-model="localDate">
        <input class="time" type="time" v-model="localTime">
    </div>
</template>

<style scoped lang="scss">
.DateTime {
    display: inline-block;
    white-space: nowrap;

    .time {
        padding-left: .5em;
    }
}
</style>
