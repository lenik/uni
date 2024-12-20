<script lang="ts">
import { computed, onMounted, ref } from "vue";

import MomentWrapper from "../../lang/time/MomentWapper";
import Instant from "../../lang/time/Instant";
import LocalDate from "../../lang/time/LocalDate";
import LocalDateTime from "../../lang/time/LocalDateTime";
import LocalTime from "../../lang/time/LocalTime";
import OffsetDateTime from "../../lang/time/OffsetDateTime";
import OffsetTime from "../../lang/time/OffsetTime";
import ZonedDateTime from "../../lang/time/ZonedDateTime";
import { SQLDate, SQLTime, Timestamp } from "../../lang/time";
import formats from "../../lang/time/formats";
import { bool } from "../types";

export interface Props {
    type?: 'date' | 'string' | 'temporal' | 'auto'
    tzType?: 'local' | 'zoned' | 'offset'
    date?: boolean // have date fields?  
    time?: boolean // have time fields?  
    zone?: boolean // have time zone fields?
    disabled?: boolean | string
}
</script>

<script setup lang="ts">
const model = defineModel<Date | SQLDate | SQLTime | Timestamp
    | MomentWrapper | LocalDate | LocalTime | LocalDateTime | OffsetTime | OffsetDateTime | ZonedDateTime
    | string | undefined | null>();

const props = withDefaults(defineProps<Props>(), {
    type: 'auto',
    tzType: undefined,
    date: undefined,
    time: undefined,
    zone: undefined,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();

const realType = computed(() => {
    switch (props.type) {
        case 'auto':
        case undefined:
            switch (typeof model.value) {
                case 'string':
                    return 'string';
                case 'object':
                    if (model.value instanceof Date)
                        return 'date';
                    if (model.value instanceof MomentWrapper)
                        return 'temporal';
                case undefined:
                case null:
                default:
                    return 'temporal';

            }
        default:
            return props.type;
    }
});

const realTzType = computed(() => {
    if (props.tzType != null)
        return props.tzType;
    switch (realType.value) {
        case 'date':
            return 'zoned';
        case 'string':
            let s = model.value as string;
            if (s != null) {
                let firstColon = s.indexOf(':');
                if (firstColon != -1) {
                    let timeAndOther = s.substring(firstColon + 1);
                    if (timeAndOther.includes('-')
                        || timeAndOther.includes('+'))
                        return 'offset';
                    let sp = timeAndOther.indexOf(' ');
                    if (sp != -1) {
                        let other = timeAndOther.substring(sp + 1);
                        if (!other.match(/^\d+$/))
                            return 'zoned';
                    }
                }
            }
            return 'local';
        case 'temporal':
            let t = model.value as MomentWrapper;
            if (t instanceof OffsetDateTime)
                return 'offset';
            if (t instanceof OffsetTime)
                return 'offset';
            if (t instanceof ZonedDateTime)
                return 'zoned';
            return 'local';
    }
});

const hasDateField = computed(() => {
    switch (realType.value) {
        case 'date':
            return true;
        case 'string':
            let s = model.value as string;
            if (s == null) return props.date;
            return s.includes('-') || s.includes('/');
        case 'temporal':
            let t = model.value as MomentWrapper;
            if (t instanceof LocalTime)
                return false;
            if (t instanceof OffsetTime)
                return false;
            return true;
    }
});

const hasTimeField = computed(() => {
    switch (realType.value) {
        case 'date':
            return true;
        case 'string':
            let s = model.value as string;
            if (s == null) return props.date;
            return s.includes(':');
        case 'temporal':
            let t = model.value as MomentWrapper;
            if (t instanceof LocalDate)
                return false;
            return true;
    }
});

const hasZoneField = computed(() => {
    switch (realTzType.value) {
        case 'local':
            return false;
        case 'offset':
        case 'zoned':
            return true;
    }
});

const showDate = computed(() => {
    if (props.date != null)
        return props.date;
    return hasDateField.value;
});

const showTime = computed(() => {
    if (props.time != null)
        return props.time;
    return hasTimeField.value;
});

const showZone = computed(() => {
    if (props.zone != null)
        return props.zone;
    return hasZoneField.value;
});

const bDisabled = computed(() => bool(props.disabled));

function parseDate(s: string | undefined | null): MomentWrapper | undefined | null {
    if (s === undefined) return undefined;
    if (s === null) return null;
    let localDate = LocalDate.parse(s);
    switch (realTzType.value) {
        case 'local':
            return localDate;
        case 'zoned':
            let instant = Instant.ofEpochMilli(localDate.epochMilli);
            let defaultTz = ZonedDateTime.now().tz!;
            return ZonedDateTime.ofInstant(instant, defaultTz);
        case 'offset':
            instant = Instant.ofEpochMilli(localDate.epochMilli);
            let utcOffset = OffsetDateTime.now().utcOffset;
            return OffsetDateTime.ofInstant(instant, utcOffset);
    }
    throw new Error("unexpected.");
}

function parseDateTime(s: string | undefined | null): MomentWrapper | undefined | null {
    if (s === undefined) return undefined;
    if (s === null) return null;
    switch (realTzType.value) {
        case 'local':
            return LocalDateTime.parse(s);
        case 'zoned':
            return ZonedDateTime.parse(s);
        case 'offset':
            return OffsetDateTime.parse(s);
    }
    throw new Error("unexpected.");
}

function parseTime(s: string | undefined | null): MomentWrapper | undefined | null {
    if (s === undefined) return undefined;
    if (s === null) return null;
    switch (realTzType.value) {
        case 'local': ``
            return LocalTime.parse(s);
        case 'zoned':
            let tz = ZonedDateTime.now().tz;
            return ZonedDateTime.parse(s, tz);
        case 'offset':
            let utcOffset = OffsetDateTime.now().utcOffset;
            return OffsetTime.parse(s, utcOffset);
    }
    throw new Error("unexpected.");
}

function toTemporal(date?: Date): MomentWrapper | null | undefined {
    if (date === undefined) return undefined;
    if (date === null) return null;
    // let time = date.getTime();
    // let offset = date.getTimezoneOffset();
    switch (realTzType.value) {
        case 'local': ``
            return new LocalDateTime(date);
        case 'zoned':
            return new ZonedDateTime(undefined, date);
        case 'offset':
            return new OffsetDateTime(undefined, date);
    }
    throw new Error("unexpected.");
}

const temporalAccess = computed({
    get() {
        switch (realType.value) {
            case 'date':
                return toTemporal(model.value as Date);
            case 'string':
                return parseDateTime(model.value as string);
            case 'temporal':
                return model.value as MomentWrapper;
        }
    },
    set(val: MomentWrapper | null | undefined) {
        switch (realType.value) {
            case 'date':
                model.value = val?.toDate();
                break;
            case 'string':
                model.value = val?.formatted;
                break;
            case 'temporal':
                model.value = val;
                break;
        }
    }
});

const localDateStr = computed({
    get(): string | null | undefined {
        let temporal = temporalAccess.value;
        if (temporal === undefined) return undefined;
        if (temporal === null) return null;
        let localDate = temporal.format(formats.UI_DATE);
        return localDate;
    },
    set(localDateStr: string | null | undefined) {
        let temporal = temporalAccess.value;
        if (localDateStr == null) {
            return;
        }
        let datePart = LocalDate.parse(localDateStr);
        if (temporal == null)
            temporal = datePart;
        else {
            temporal.year = datePart.year;
            temporal.month = datePart.month;
            temporal.dayOfMonth = datePart.dayOfMonth;
        }
        temporalAccess.value = temporal;
    }
});

const localTimeStr = computed({
    get(): string | null | undefined {
        let temporal = temporalAccess.value;
        if (temporal === undefined) return undefined;
        if (temporal === null) return null;
        let localTime = temporal.format(formats.UI_TIME);
        return localTime;
    },
    set(localTimeStr: string | null | undefined) {
        let temporal = temporalAccess.value;
        if (localTimeStr == null) {
            return;
        }
        let timePart = LocalTime.parse(localTimeStr);
        if (temporal == null)
            temporal = timePart;
        else {
            temporal.hour = timePart.hour;
            temporal.minute = timePart.minute;
            temporal.second = timePart.second;
            temporal.millisecond = timePart.millisecond;
        }
        temporalAccess.value = temporal;
    }
});

const timeZoneStr = computed({
    get(): string | null | undefined {
        let temporal = temporalAccess.value;
        if (temporal === undefined) return undefined;
        if (temporal === null) return null;
        return temporal.timeZone;
    },
    set(zoneId: string | null | undefined) {
        let temporal = temporalAccess.value;
        if (zoneId == null) {
            return;
        }
        if (temporal != null)
            temporal.timeZone = zoneId;
        temporalAccess.value = temporal;
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
    <ul class="debug">
        <li> realType: {{ realType }} </li>
        <li> realTzType: {{ realTzType }} </li>
        <li> hasDateField: {{ hasDateField }} </li>
        <li> hasTimeField: {{ hasTimeField }} </li>
        <li> hasZoneField: {{ hasZoneField }} </li>
    </ul>
    <div class="DateTime" ref="rootElement">
        <input class="date" type="date" :disabled="bDisabled" v-model="localDateStr" v-if="showDate">
        <input class="time" type="time" :disabled="bDisabled" v-model="localTimeStr" v-if="showTime">
        <input class="zone" type="text" :disabled="bDisabled" v-model="timeZoneStr" v-if="showZone">
        <slot></slot>
    </div>
</template>

<style scoped lang="scss">
.DateTime {
    display: flex;
    flex-direction: row;
    white-space: nowrap;

    input {
        font-size: inherit;
    }

    .time {
        margin-left: .5em;
    }

    .zone {
        margin-left: .5em;
        width: 4em;
        font-size: 80%;
    }
}

.debug {
    display: none;
    list-style: none;
    margin: 0;
    padding: 0;
    color: gray;
    font-size: 80%;

    li {
        display: inline-block;

        &:not(:first-child)::before {
            content: ', ';
        }
    }
}
</style>
