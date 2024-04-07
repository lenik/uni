<script lang="ts">
import { computed, inject, onMounted, provide, ref } from 'vue';

import type { int } from '@skeljs/core/src/lang/basetype';
import { showError } from '@skeljs/core/src/logging/api';

import EntityType from '../../net/bodz/lily/entity/EntityType';
import { SERVER_URL } from './context';
import "blueimp-file-upload-esm/css/jquery.fileupload.css";
import "blueimp-file-upload-esm";

import $ from 'jquery';

export interface Props {
    serviceUrl?: string // injectable
    type: EntityType
    url?: string
    subDir?: string
    maxSpeed?: int
    maxChunkSize?: int
}
</script>

<script setup lang="ts">
import FieldRow, { FIELD_ROW_PROPS } from '@skeljs/core/src/ui/FieldRow.vue';

provide(FIELD_ROW_PROPS, {
    labelWidth: '8em',
    alignLabel: 'right'
});

const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
    maxSpeed: 0,
    maxChunkSize: 0,
});

const emit = defineEmits<{
    error: [message: string]
    done: [result: any]
}>();

// property shortcuts

const serverUrl = inject<string>(SERVER_URL)!;
const realServiceUrl = computed(() => {
    let url: string;
    if (props.serviceUrl != null)
        url = props.serviceUrl;
    else if (serverUrl != null)
        url = serverUrl + "/" + simpleName.value + "/import";
    else
        return undefined;
    return url;
});

const entityClass = computed(() => props.type.name);
const simpleName = computed(() => {
    let className = entityClass.value;
    let lastDot = className.lastIndexOf('.');
    return lastDot == -1 ? className : className.substring(lastDot + 1);
});

const fullServiceUrl = computed(() => {
    let url = realServiceUrl.value;
    let params: any = {
        delim: csvDelimitor.value,
        encoding: encoding.value,
        maxSpeed: props.maxSpeed,
    };
    let i = 0;
    for (let k in params) {
        if (i++ == 0)
            url += "?";
        else
            url += "&";
        let val = params[k];
        let encoded = encodeURI(val);
        url += k + "=" + encoded;
    }
    return url;
});

// app state

const csvDelimitor = ref<string>(',');
const encoding = ref<string>('utf-8');

const result = ref();

// DOM references

const rootElement = ref<HTMLElement>();
const fileInput = ref<HTMLInputElement>();
const progressDiv = ref<HTMLElement>();
const completionDiv = ref<HTMLElement>();

// methods

defineExpose({ reset });

function reset() {
    result.value = null;
}

function setPercent(percent: number) {
    let $comp = $(completionDiv.value!);
    $comp.text(percent + "%");
    $comp.css("width", percent + "%");
}

onMounted(() => {
    let $input = $(fileInput.value!) as any;
    let fileupload = $input.fileupload({
        dataType: 'json',
        maxChunkSize: props.maxChunkSize,
        submit: function () {
            setPercent(0);
            $(progressDiv.value!).show();
            // beginWait();
        },
        always: function () {
            // endWait();
            $(progressDiv.value!).hide();
        },
        progressall: function (e: JQuery.Event, data: any) {
            let percent = Math.floor(data.loaded / data.total * 1000) / 10;
            setPercent(percent);
        },
        done: function (e: JQuery.Event, data: any) {
            result.value = data.result;
            emit('done', result.value);
        }
    });
});
</script>

<template>
    <div class="import-panel" ref="rootElement" :class="{ uploaded: result != null }">
        <div class="upload">
            <div> Select file to import:</div>
            <div class="control">
                <input type="file" ref="fileInput" class="upload" :data-url="fullServiceUrl">
                <div class="filler"></div>
                <div class="progress" ref="progressDiv">
                    <div class="completion" ref="completionDiv">0%</div>
                </div>
            </div>
            <div class="options" v-if="result == null">
                <div class="header">Options:</div>
                <FieldRow label="Encoding" v-model="encoding">
                    <select v-model="encoding">
                        <option value="utf-8">UTF-8</option>
                        <option value="gbk">GBK</option>
                        <option value="gb2312">GB2312</option>
                    </select>
                </FieldRow>
                <FieldRow label="CSV Delimitor" v-model="csvDelimitor">
                    <input type="text" maxlength="1" size="2" v-model="csvDelimitor">
                </FieldRow>
            </div>
        </div>
    </div>
    <div class="result" v-if="result != null">
        <div class="header">Result:</div>
        <div class="error" v-if="result.failed">
            <ul>
                <li>Status: {{ result.status }}</li>
                <li>Error Message: {{ result.message }}</li>
            </ul>
        </div>
        <ul class="result-data" v-if="result.data != null">
            <li class="inserts" v-if="result.data.inserts.length"> {{ result.data.inserts.length }} inserted: <ol>
                    <li v-for="(obj, i) in result.data.inserts">
                        <slot name="item" v-bind="{ o: obj, ...obj }">{{ obj }}</slot>
                    </li>
                </ol>
            </li>
            <li class="updates" v-if="result.data.updates.length"> {{ result.data.updates.length }} updated: <ol>
                    <li v-for="(obj, i) in result.data.updates">
                        <slot name="item" v-bind="{ o: obj, ...obj }">{{ obj }}</slot>
                    </li>
                </ol>
            </li>
            <li class="errors" v-if="result.data.errors.length"> {{ result.data.errors.length }} errors: <ol>
                    <li v-for="(err, i) in result.data.errors">
                        <slot name="error" v-bind="{ e: err }">{{ err }}</slot>
                    </li>
                </ol>
            </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}

.uploaded {
    .upload {
        display: none;
    }
}

.control {
    margin: 1em;
    display: flex;
    flex-direction: row;

}

.filler {
    flex: 1;
}

.progress {
    display: none;
    background: #eee;
    border-radius: 5px;
    border: solid 1px #888;
    overflow: hidden;
    padding: 2px;
    width: 5em;

    .completion {
        background: #0af;
        width: 0%;
        height: 1.5em;
        border-radius: 4px;
        padding: 2px;
        font-size: 0.7em;
        font-family: monospace;
        text-align: center;
        color: #fff;
        box-sizing: border-box;
    }
}

.options {
    margin: 1em 0;

    .header {
        border-bottom: solid 1px #ccc;
    }

    .field-row {
        margin: .5em 0;
    }
}

.error {
    color: red;
}

.result-data {
    // list-style: none;
    // margin: 0;
}
</style>
