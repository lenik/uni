<script lang="ts">
import { computed, onMounted, ref } from "vue";
import { Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, OffsetTime, ZonedDateTime } from '../src/lang/time';

export const title = "DateTime for various timezone";

export interface Props {
}
</script>

<script setup lang="ts">
import DateTime from '../src/ui/input/DateTime.vue';
import FieldRow from '../src/ui/FieldRow.vue';

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();

const fieldRowProps = {
    align: 'top',
    alignLabel: 'left',
    labelWidth: '5em',
    // required: true,
    // watch: false,
};

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});

const date = ref(new Date());
const dateStr = computed({
    get() { return date.value.toString(); },
    set(s: string) { date.value = new Date(s); }
});

const zonedNow = ZonedDateTime.now();
const offsetNow = OffsetDateTime.now();
const localNow = LocalDateTime.now();

const zonedStr = ref(zonedNow.isoFormat);
const offsetStr = ref(offsetNow.isoFormat);
const localStr = ref(localNow.isoFormat);

const instant = ref(Instant.now());
const localDate = ref(LocalDate.now());
const localDateTime = ref(LocalDateTime.now());
const localTime = ref(LocalTime.now());
const offsetDateTime = ref(OffsetDateTime.now());
const offsetTime = ref(OffsetTime.now());
const zonedDateTime = ref(ZonedDateTime.now());

</script>

<template>
    <div class="component-root" ref="rootElement">
        <ul>
            <li>
                <FieldRow v-bind="fieldRowProps" label="date" v-model="date">
                    <DateTime v-model="date">
                        <i></i> <input type="text" v-model="date" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="local-str" v-model="localStr">
                    <DateTime v-model="localStr">
                        <i></i> <input type="text" v-model="localStr" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="zoned-str" v-model="zonedStr">
                    <DateTime v-model="zonedStr">
                        <i></i> <input type="text" v-model="zonedStr" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="offset-str" v-model="offsetStr">
                    <DateTime v-model="offsetStr">
                        <i></i> <input type="text" v-model="offsetStr" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="temporal / instant" v-model="instant">
                    <DateTime v-model="instant">
                        <i></i> <input type="text" v-model="instant.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="temporal / localDate" v-model="localDate">
                    <DateTime v-model="localDate">
                        <i></i> <input type="text" v-model="localDate.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="Datetemporal / localDateTime" v-model="localDateTime">
                    <DateTime v-model="localDateTime">
                        <i></i> <input type="text" v-model="localDateTime.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="temporal / localTime" v-model="localTime">
                    <DateTime v-model="localTime">
                        <i></i> <input type="text" v-model="localTime.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="temporal / offsetDateTime" v-model="offsetDateTime">
                    <DateTime v-model="offsetDateTime">
                        <i></i> <input type="text" v-model="offsetDateTime.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="temporal / offsetTime" v-model="offsetTime">
                    <DateTime v-model="offsetTime">
                        <i></i> <input type="text" v-model="offsetTime.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
            <li>
                <FieldRow v-bind="fieldRowProps" label="temporal / zonedDateTime" v-model="zonedDateTime">
                    <DateTime v-model="zonedDateTime">
                        <i></i> <input type="text" v-model="zonedDateTime.formatted" />
                    </DateTime>
                </FieldRow>
            </li>
        </ul>
    </div>
</template>

<style lang="scss">
.main {
    display: flex;
    flex-direction: column;
}
</style>

<style scoped lang="scss">
.component-root {
    columns: 2;
}

ul {
    flex: 1;
    display: flex;
    flex-direction: column;

    list-style: none;
    margin: 0;
    padding: 0;

    >li {
        margin-bottom: 1em;
        border: solid 1px #888;
        background-color: #fef;
        padding: .5em 1em;
    }

    i {
        flex: 1;
    }

    input[type=text] {
        color: gray;
        font-style: italic;
        outline: none;
        border: solid 1px gray;
        background-color: #eff;
        width: 10em;
    }

}
</style>
