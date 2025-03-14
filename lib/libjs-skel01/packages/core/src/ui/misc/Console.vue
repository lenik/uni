<script lang="ts">
const title = "component Console";

import { computed, onMounted, ref } from "vue";
import stringArgv from 'string-argv';

import { BufferedConsole } from "./console";

type MainFunc = (...args: string[]) => void;

export interface Props {
    main?: MainFunc
}
</script>

<script setup lang="ts">
import Icon from "../Icon.vue";

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();


// property shortcuts

// app state
const cmdline = ref("");
const argv = computed(() => stringArgv(cmdline.value));

const lines = ref([]);
const value = ref<any>();

// DOM references
const rootElement = ref<HTMLElement>();

// methods
defineExpose({ update });

async function update() {
    let mainFn = props.main || window.main;
    if (mainFn == null)
        throw new Error("main function isn't specified");
    let args = argv.value;
    let console0 = window.console;
    let buf = new BufferedConsole();
    window.console = buf;
    try {
        let ans = mainFn(...args);

        if (ans instanceof Promise) {
            // console.debug('async main, await for it');
            ans = await ans;
        }
        if (ans != undefined)
            console.log('Result:', ans);
    } catch (err) {
        buf.error('Uncaught error:', err);
    } finally {
        window.console = console0;
    }
    lines.value = buf.flatten();
    value.value = buf.value;
}

onMounted(update);

</script>

<template>
    <div class="console">
        <div class="topbar">
            <Icon name="fa-terminal" />
            <input type="text" v-model="cmdline" placeholder="Comand line arguments...">
            <button @click="update">
                <Icon id="run-state" name="fa-refresh" /> Run
            </button>
        </div>
        <ul class="args">
            <li v-for="(arg, i) in argv" :key="i"> {{ arg }} </li>
        </ul>
        <hr>
        <ul class="lines" ref="rootElement">
            <li v-for="(line, i) in lines" :key="i" :class="line.type">
                <span class="time" :title="line.time.format('YYYY-MM-DD')">{{ line.time.format('HH:mm:ss.SSSSSS')
                    }}</span>
                <div class="indent" :style="{ 'margin-left': (line.indentLevel * 2) + 'em' }">
                    <span class="message" v-if='line.message != null'>{{ line.message }}</span>
                    <ul class="fields" v-if="line.fields.length">
                        <li class="field" v-for="field in line.fields">
                            <span class="undefined" v-if="field === undefined"> undefined </span>
                            <span class="null" v-else-if="field === null"> null </span>
                            <span class="string" v-else-if="typeof field == 'string'"> {{ field }} </span>
                            <span class="number" v-else-if="typeof field == 'number'"> {{ field }} </span>
                            <span class="array" v-else-if="Array.isArray(field)"> {{ field }} </span>
                            <span class="object" v-else> {{ field }} </span>
                        </li>
                    </ul>
                </div>
            </li>
        </ul>
        <hr>
        <div class="value-view">
            <slot :value="value"></slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.console {
    display: flex;
    flex-direction: column;
    border: solid 1px gray;
    background-color: hsl(160, 50%, 98%);
    padding: 1em 1em 0;
    overflow: hidden;

    hr {
        border: none;
        border-top: solid 1px lightgray;
        margin: 0 -1em;
    }

    .lines {
        flex: 1;
    }
}

.topbar {
    display: flex;
    flex-direction: row;
    gap: 1em;

    .icon {
        color: gray;
    }

    input {
        flex: 1;
    }
}

.args {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: .5em 1em;
    list-style: none;
    margin: 0 -1em;
    padding: .5em 1em;
    // border-bottom: solid 1px gray;

    >li {
        border: solid 1px gray;
        padding: .2em .5em;
        background-color: hsl(180, 30%, 95%);
    }
}

.lines {
    list-style: none;
    margin: 0;
    padding: 0;

    >li {
        margin: 0;
        padding: .1em 0;
        box-sizing: border-box;

        &:not(:first-child) {
            border-top: dashed 1px hsl(200, 70%, 80%);
        }

        &:nth-child(even) {
            background-color: hsl(200, 70%, 97%);
        }

        &.error {
            color: red;
        }

        &.warn {
            color: pink;
        }

        &.info {
            color: green;
        }

        &.log {
            font-weight: 300;
            color: darkgray;
        }

        &.debug {
            font-weight: 300;
            color: gray;
        }

        &.trace {
            font-weight: 300;
            color: lightgray;
        }
    }

    .time {
        font-size: 80%;
        color: hsl(100, 50%, 40%);
        margin-right: 1em;
        vertical-align: top;
    }

    .indent {
        display: inline-block;
    }

    .message {
        &:after~* {
            content: '◥';
            font-size: 50%;
            vertical-align: top;
            margin-left: .5em;
            opacity: 50%;
        }
    }

    .fields {
        list-style: none;
        margin: 0;
        padding: 0;

        >li {
            margin: 0;
        }

        .undefined,
        .null {
            font-family: monospace;
            font-style: italic;
            color: lightgray;
        }

        .string {
            color: hsl(210, 100%, 60%);
        }

        .number {
            color: hsl(190, 100%, 45%);
        }

        .array {
            color: hsl(100, 80%, 30%);
        }

        .object {
            color: hsl(300, 80%, 60%);
        }
    }
}

.value-view {
    margin: 0;
    padding: 0;
    // border-top: solid 1px gray;
    // background-color: pink;
}
</style>
