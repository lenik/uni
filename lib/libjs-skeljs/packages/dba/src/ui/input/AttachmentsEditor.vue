<script lang="ts">
import $ from 'jquery';
import { computed, inject, onMounted, ref } from "vue";
import { int } from "../../../../core/src/lang/basetype";
import { SERVER_URL } from "../lily/context";
import Attachment from "@skeljs/core/src/net/bodz/lily/entity/Attachment";

import { beginWait, endWait } from "@skeljs/core/src/skel/waitbox";

import "blueimp-file-upload/css/jquery.fileupload.css";
import "blueimp-file-upload";

export interface Props {
    serviceUrl?: string // injectable
    className: string
    //obj?: IHaveAttachments
    id: any
    subDir?: string
}
</script>

<script setup lang="ts">
import Icon from "@skeljs/core/src/ui/Icon.vue";

const model = defineModel<Attachment[]>();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    // error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const fileInput = ref<HTMLInputElement>();
const progressDiv = ref<HTMLElement>();
const completeDiv = ref<HTMLElement>();

const serverUrl = inject<string>(SERVER_URL)!;
const realServiceUrl = computed(() => {
    let url: string;
    if (props.serviceUrl != null)
        url = props.serviceUrl;
    else if (serverUrl != null)
        url = serverUrl + "/upload";
    else
        return undefined;
    return url;
});

const simpleName = computed(() => {
    let className = props.className;
    let lastDot = className.lastIndexOf('.');
    return lastDot == -1 ? className : className.substring(lastDot + 1);
});

const fileClass = computed(() => {
    let d = simpleName.value;
    if (props.subDir != null)
        if (d == null)
            d = props.subDir;
        else
            d += "/" + props.subDir;
    return d;
});

const serviceUploadUrl = computed(() => {
    let url = realServiceUrl.value;
    return url + "?class=" + fileClass.value + "/tmp";
});

const indexUrl = computed(() => {
    return serverUrl + "/" + simpleName.value;
});

// methods

defineExpose({ update });

function update() {
}

function idHref(a: Attachment, id: string | number) {
    if (a == null) return null;
    return a.idHref(id);
}

function url(a: Attachment, id: string | number) {
    let href = idHref(a, id);
    let url = indexUrl.value + "/" + href;
    return url;
}

function remove(i: int) {
    let attachments = model.value;
    if (attachments != null)
        attachments.splice(i, 1);
}

onMounted(() => {
    let $input = $(fileInput.value!) as any;
    $input.fileupload({
        dataType: 'json',
        submit: function () {
            $(progressDiv.value!).show();
            beginWait();
        },
        always: function () {
            endWait();
            $(progressDiv.value!).hide();
        },
        progress: function (e, data) {
            let percent = Math.floor(data.loaded / data.total * 1000) / 10;
            let $comp = $(completeDiv.value!);
            $comp.text(percent + "%");
            $comp.css("width", percent + "%");
        },
        done: function (e: JQuery.Event, data) {
            data.result.files.forEach(function (file) {
                var attachment = new Attachment({
                    name: file.name,
                    size: file.size,
                    sha1: file.sha1,
                    href: file.url
                });
                if (model.value == null)
                    model.value = [];
                model.value.push(attachment);
            });
        }
    });

});
</script>

<template>
    <div class="attachments-editor" ref="rootElement">
        <input type="file" ref="fileInput" class="upload" :data-url="serviceUploadUrl">
        <div class="progress" ref="progressDiv">
            <div class="text" ref="progressText">0%</div>
        </div>
        <ul class="previews" v-if="model != null">
            <li v-for="(attachment, i) in model" :key="i">
                <img :src="url(attachment, id)">
                <div class="caption">{{ attachment.name }}
                    <Icon name="far-trash" @click="remove(i)" />
                </div>
            </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
.attachments-editor {
    padding: 0;
}

.progress {
    display: inline-block;
    background: #eee;
    border-radius: 5px;
    border: solid 1px #888;
    overflow: hidden;
    padding: 2px;
    width: 5em;

    >div {
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

.previews {
    display: inline-block;
    list-style: none;
    margin: 0;
    padding: 0;
    // border: dashed 1px gray;
    background-color: #ffe;

    li {
        margin: .5em;
        border: solid 1px gray;
        text-align: center;

        img {
            max-width: 4em;
            max-height: 4em;
            object-fit: contain;
        }

        .caption {
            word-break: break-all;
            max-width: 12em;
            font-size: 80%;
        }
    }
}
</style>
