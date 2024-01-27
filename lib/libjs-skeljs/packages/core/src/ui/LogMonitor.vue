<script setup lang="ts">

import { onMounted, ref } from "vue";
import { Moment } from "moment";

// import { } from "./types";
import { LogEntry, levelIcon, simpleName, causes } from "../logging/api";

import Icon from './Icon.vue';

const model = defineModel();

interface Props {
    src: LogEntry[]
}

const props = withDefaults(defineProps<Props>(), {
});

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement>();
onMounted(() => {
});

function update() {
}

defineExpose({ update });

function formatDate(time: Moment) {
    return time.format('YYYY-MM-DD');
}
function formatTime(time: Moment) {
    return time.format('HH:mm:ss.SSS');
}

</script>

<template>
    <ul class="log-monitor" ref="rootElement">
        <li class="log" v-for="(log, i) in src" :key="i" :level="log.level" :class="{ expanded: log.expanded }">
            <div class="line">
                <Icon :name="levelIcon(log.level)" />
                <span class="date">{{ formatDate(log.time) }}</span>
                <span class="time">{{ formatTime(log.time) }}</span>
                <p>{{ log.message }}</p>
                <Icon :name="log.expanded ? 'fas-question-circle' : 'far-question-circle'" class="toggler"
                    @click="log.expanded = !log.expanded" v-if="log.exception != null" />
            </div>
            <ol class="causes" v-if="log.exception != null && log.expanded">
                <li class="exception" v-for="(cause, i) in causes(log.exception)" :key="i">
                    <div class="e-hdr" @click="cause.expanded = !cause.expanded">
                        <span class="label"> {{ simpleName(log.exception.type) }}</span>
                        <p>{{ log.exception.message }}</p>
                        <Icon :name="cause.expanded ? 'fas-chevron-down' : 'fas-chevron-right'" />
                    </div>
                    <ol class="stack-trace" v-if="cause.stackTrace != null && cause.expanded">
                        <li v-for="(ste, i) in cause.stackTrace" :key="i">
                            <span class="module" v-if="ste.module != null">{{ ste.module }}</span>
                            <span class="class">{{ ste.className }}</span>
                            <span class="method">{{ ste.methodName }}</span>
                            <span class="file" v-if="ste.file != null && ste.line != null">{{ ste.file }}</span>
                            <span class="line" v-if="ste.file != null && ste.line != null">{{ ste.line }} </span>
                        </li>
                    </ol>
                </li>
            </ol>
        </li>
    </ul>
</template>

<style scoped lang="scss">
.log-monitor {
    margin: 0;
    padding: 0;
    list-style: none;
}

.log[level=trace] {
    color: hsl(240, 30%, 65%);
    font-weight: 300;
}

.log[level=debug] {
    color: hsl(210, 40%, 50%);
    font-weight: 300;
}

.log[level=log] {
    color: hsl(180, 40%, 40%);
}

.log[level=info] {
    color: hsl(150, 40%, 30%);
}

.log[level=warn] {
    color: hsl(0, 60%, 70%);
}

.log[level=error] {
    color: hsl(0, 80%, 50%);
    font-weight: 400;
}

.log[level=fatal] {
    color: hsl(0, 100%, 30%);
    font-weight: 400;
}

.log:not(.expanded) {
    .line .toggler {
        color: #bbb;
    }
}

.line {
    display: flex;
    flex-direction: row;
    align-items: center;

    .icon {
        width: 1.8em;
        text-align: center;
    }

    .date,
    .time {
        white-space: nowrap;
    }

    .date {
        display: flex;
        flex-direction: row;
        justify-content: right;
        width: 2em;
        overflow: hidden;
        color: #ccc;
        position: relative;
        font-size: 70%;
        top: -.3em;
    }

    .time {
        color: #999;
        position: relative;
        font-size: 70%;
        top: .4em;
    }

    p {
        margin: 0 0 0 .5em;
        flex: 1;
    }
}

.causes {
    margin: .3em 0 .7em 2em;
    padding-left: 0.5em;
    list-style: none;
    border-left: dashed 1px gray;
}

.exception {
    .e-hdr {
        display: flex;
        flex-direction: row;
        align-items: center;

        .label {
            font-weight: 500;

            &::after {
                content: ': ';
            }
        }

        p {
            margin: 0 0 0 .5em;
        }

        .icon {
            font-size: 80%;
            flex: 1;
            // text-align: right;
            margin-left: 1em;
        }
    }
}

.stack-trace {
    margin: 0;
    padding: 0 0 0 2em;
    list-style: none;
    font-weight: 300;
    font-size: 90%;
    color: hsl(200, 30%, 50%);

    --module-color: hsl(200, 10%, 30%);
    --loc-color: hsl(180, 70%, 40%);

    li {
        display: flex;
        flex-direction: row;

        .module {
            color: var(--module-color);

            &::after {
                color: var(--module-color);
                content: "/";
            }
        }

        .method {
            font-weight: 400;

            &::before {
                content: ".";
            }

            &::after {
                content: " ";
            }
        }

        .file,
        .line {
            color: var(--loc-color);
            font-weight: 400;
        }

        .file {
            &:before {
                content: " (";
                margin-left: .8em;
                color: var(--loc-color);
            }
        }

        .line {
            &:before {
                content: ": ";
                color: var(--loc-color);
            }

            &:after {
                content: ")";
                color: var(--loc-color);
            }
        }
    }
}
</style>
