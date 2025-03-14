<script lang="ts">
import $ from 'jquery';
import { computed, inject, onMounted, ref } from "vue";
import { int } from "../../../../core/src/lang/basetype";
import { SERVER_URL } from "../lily/context";
import Attachment from "skel01-core/src/net/bodz/lily/entity/Attachment";
import { beginWait, endWait } from "skel01-core/src/skel/waitbox";
import "blueimp-file-upload/css/jquery.fileupload.css";
import "blueimp-file-upload";

export interface Props {
    serviceUrl?: string // injectable
    className: string
    //obj?: IHaveAttachments
    id: any
    subDir?: string
    maxSpeed?: int
    maxChunkSize?: int
}
</script>

<script setup lang="ts">
import Icon from "skel01-core/src/ui/Icon.vue";
import MediaView from "skel01-core/src/ui/MediaView.vue";

const model = defineModel<Attachment[]>();

const props = withDefaults(defineProps<Props>(), {
    maxSpeed: 0,
    maxChunkSize: 0,
});

const emit = defineEmits<{
    // error: [message: string]
}>();

// property shortcuts

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
    url += "?class=" + fileClass.value;
    if (props.maxSpeed != null)
        url += "&maxSpeed=" + props.maxSpeed;
    return url;
});

const indexUrl = computed(() => {
    return serverUrl + "/" + simpleName.value;
});

// app states

const scaleFactor = ref(1.00);

// DOM references

const rootElement = ref<HTMLElement>();
const fileInput = ref<HTMLInputElement>();
const progressDiv = ref<HTMLElement>();
const completionDiv = ref<HTMLElement>();
const previewsDiv = ref<HTMLElement>();

// methods

defineExpose({ update });

function update() {
}

function idHref(a: Attachment, id: string | number) {
    if (a == null) return null;
    return a.idHref(id);
}

function attachmentUrl(a: Attachment, id: string | number) {
    let url: string;
    if (a.path != null)
        url = serverUrl + a.path;
    else
        url = indexUrl.value + "/" + idHref(a, id);
    return url;
}

function remove(i: int) {
    let attachments = model.value;
    if (attachments != null)
        attachments.splice(i, 1);
}

function setPercent(percent: number) {
    let $comp = $(completionDiv.value!);
    $comp.text(percent + "%");
    $comp.css("width", percent + "%");
}

function scaleMaxSize(elm: HTMLElement, scale: number) {
    let map = elm.computedStyleMap();
    let maxWidth = map.get('max-width')!;
    let maxHeight = map.get('max-height')!;

    if (maxWidth == 'none')
        maxWidth = map.get('width')!;
    if (maxHeight == 'none')
        maxHeight = map.get('height')!;

    if (maxWidth instanceof CSSNumericValue)
        maxWidth = maxWidth.mul(scale);
    if (maxHeight instanceof CSSNumericValue)
        maxHeight = maxHeight.mul(scale);
    elm.style.maxWidth = maxWidth.toString();
    elm.style.maxHeight = maxHeight.toString();
}

function onPreviewClick(attachment: Attachment, i: number, e: MouseEvent) {
    switch (e.button) {
        case 1:     // middle button
            remove(i);
            e.preventDefault();
            break;
    }
}

function onPreviewWheel(attachment: Attachment, i: number, e: WheelEvent) {
    // console.log(e.deltaMode, e.deltaX, e.deltaY, e.deltaZ);
    // console.log(WheelEvent.DOM_DELTA_PIXEL, WheelEvent.DOM_DELTA_LINE, WheelEvent.DOM_DELTA_PAGE);
    let target = e.target as HTMLElement;
    let mediaView = target.closest('.media-view');
    if (mediaView != null) target = mediaView as HTMLElement;

    let scale = -Math.round(e.deltaY * 0.01);
    // scale = 1 + scale * 0.1;
    scaleFactor.value += scale * 0.1;
    // scaleMaxSize(target, scale);
    e.preventDefault();
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
        progressall: function (e: JQuery.Event, data) {
            let percent = Math.floor(data.loaded / data.total * 1000) / 10;
            setPercent(percent);
        },
        done: function (e: JQuery.Event, data) {
            data.result.files.forEach(function (file) {
                var attachment = new Attachment({
                    path: file.url,
                    name: file.name,
                    size: file.size,
                    sha1: file.sha1,
                    href: file.url
                });
                let list = model.value;
                if (list == null)
                    list = model.value = [];
                list.push(attachment);
            });
        }
    });
});

</script>

<template>
    <div class="attachments-editor" ref="rootElement">
        <div class="control">
            <input type="file" ref="fileInput" class="upload" :data-url="serviceUploadUrl">
            <div class="filler"></div>
            <div class="progress" ref="progressDiv">
                <div class="completion" ref="completionDiv">0%</div>
            </div>
        </div>
        <ul class="previews" v-if="model != null" ref="previewsDiv">
            <li v-for="(attachment, i) in model" :key="i"
                @mousedown="(e: MouseEvent) => onPreviewClick(attachment, i, e)">
                <MediaView :src="attachmentUrl(attachment, id)"
                    @wheel="(e: WheelEvent) => onPreviewWheel(attachment, i, e)" />
                <div class="caption">{{ attachment.path }}
                    <Icon name="far-trash" @click="remove(i)" />
                </div>
            </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
.attachments-editor {
    display: flex;
    flex-direction: column;
    width: 100%;

    .control {
        display: flex;
        flex-direction: row;

    }

    .filler {
        flex: 1;
    }
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

.previews {
    display: inline-flex;
    flex-direction: row;
    align-items: stretch;
    list-style: none;
    margin: 0;
    padding: 0;
    // border: dashed 1px gray;
    background-color: #ffffee;

    --scale-factor: v-bind(scaleFactor);

    li {
        border: dashed 1px gray;
        margin-top: .3em;
        padding: .5em .8em;
        text-align: center;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-end;

        &:not(:first-child) {
            margin-left: .5em;
        }

        .media-view {
            // img,
            // video,
            // div.unknown {
            max-width: calc(4em * var(--scale-factor));
            max-height: calc(4em * var(--scale-factor));
            // object-fit: contain;
            flex: 1;
        }

        .caption {
            word-break: break-all;
            max-width: 12em;
            font-size: 80%;
        }
    }
}
</style>
