<script lang="ts">
import { computed, inject, onMounted, ref, watch, watchEffect } from "vue";
import type { int } from "skel01-core/src/lang/basetype";
import Attachment from "skel01-core/src/net/bodz/lily/entity/Attachment";
import { isImageName } from "skel01-core/src/util/mime";
// import { IdEntity } from "../../net/bodz/lily/concrete/IdEntity";

import { SERVER_URL } from '../lily/context';
import IEntityType from "../../net/bodz/lily/entity/IEntityType";
import EntityType from "../../net/bodz/lily/entity/EntityType";
import { ColumnOrders, SelectOptions } from "../../net/bodz/lily/entity/EntityController";
import type { IDialog } from "skel01-core/src/ui/types";

export interface Props {
    type: EntityType
    brief?: boolean | 'auto';

    dialogButton?: boolean  // in brief-entry
    dialog?: IDialog

    removeButton?: boolean  // in brief-entry
    rightRemove?: boolean   // in brief-entry: right-click to deselect

    quick?: boolean | 'auto';
    buttonMax?: int         // number of candidates to show on the quick select bar

    criteria?: any
    orders?: ColumnOrders

    serverUrl?: string
    url?: string
}

</script>

<script setup lang="ts">
import Icon from "skel01-core/src/ui/Icon.vue";
import MediaView from "skel01-core/src/ui/MediaView.vue";

const model = defineModel<any>();
const idModel = defineModel("id");

const props = withDefaults(defineProps<Props>(), {
    brief: 'auto',
    dialogButton: false,
    removeButton: true,
    rightRemove: true,
    quick: 'auto',
    buttonMax: 100,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const unspecified = computed(() => justNulls(model.value));
const specified = computed(() => !unspecified.value);

const selectedId = computed(() => model.value?.id || idModel.value);
const hasId = computed(() => model.value?.id != null || idModel.value != null);
const hasLabel = computed(() => model.value?.label != null);

function justNulls(o) {
    if (o == null)
        return true;
    if (typeof o != 'object')
        return false;
    return o.id == null && o.label == null;
}

// states

const serverUrl = inject<string>(SERVER_URL)!;
const simpleName = computed(() => props.type.simpleName);
const indexUrl = computed(() => {
    return serverUrl + "/" + simpleName.value;
});

const data = ref();

const listed = computed(() => {
    let selectedId = model.value?.id || idModel.value;
    if (selectedId == null) return false;

    if (data.value == null) return false;
    for (let item of data.value.list) {
        if (item.id == selectedId)
            return true;
    }
    return false;
});

const unionList = computed(() => {
    let union = [] as any[];
    if (model.value != null)
        if (!listed.value)
            union.push(model.value);
    if (data.value != null) {
        let list = data.value.list as any[];
        let max = Math.min(props.buttonMax, list.length);
        let head = list.slice(0, max)
        union.push(...head);
    }
    return union;
});

const hasMore = computed(() => {
    let d = data.value;
    if (d == undefined) return undefined;
    if (d.rowCount <= props.buttonMax)
        return false;
    return true;
});

// const noMore = computed(() => !hasMore.value);
const showBrief = computed(() => props.brief === true || props.quick === false || (props.brief == 'auto' && hasMore.value));
const showQuick = computed(() => props.quick === true || props.quick == 'auto');

// methods

defineExpose({ update });

function update() {
}

function select(item: any) {
    model.value = item;
    idModel.value = item?.id;
}

function remove() {
    model.value = undefined;
    idModel.value = undefined;
}

function openDialog() {
    props.dialog?.open({
        onselect: onDialogSelect,
    });
}

function onDialogSelect(val: any) {
    console.log('dialog select', val);
    model.value = val;
    idModel.value = val?.id;
    return true;
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

onMounted(() => {
    watchEffect(async () => {
        if (!showQuick.value) return [];
        let ec = props.type.controller(serverUrl);
        let limit = props.buttonMax + 1;
        let opts: SelectOptions = new SelectOptions(0, limit);
        opts.orders = props.orders;
        let a = await ec.filter(props.criteria, opts);
        data.value = a;
    });
});
</script>

<template>
    <span class="ref-editor" ref="rootElement" :class="{ specified, unspecified, hasId, hasLabel }">
        <!-- <input type="hidden" :value="model?.id || id"> -->
        <!-- <div> model: {{ model?.id }}. {{ model?.label }} </div>
        <div>id: {{ id }}</div> -->
        <ul class="quick-select" v-if="showQuick && data != null">
            <li v-for="(item, i) in unionList" :key="i" :data-id="item.id" :class="{
                selected: item.id == selectedId,
                'not-listed': item.id == selectedId && !listed,
            }" @click="select(item)">
                <slot name="quick-item">
                    <ul class="images" v-if="item.files?.images">
                        <li v-for="(attachment, i) in item.files.images" :key="i">
                            <MediaView :src="attachmentUrl(attachment, item.id)" />
                        </li>
                    </ul>
                    <div class="text">
                        <Icon :name="item.icon" v-if="item.icon != null" />
                        <span class="id">{{ item.id }}</span>
                        <span class="label">{{ item.label }} </span>
                    </div>
                </slot>
            </li>
            <li v-if="hasMore" @click="openDialog"> ... </li>
        </ul>
        <div class="brief-entry" v-if="showBrief">
            <div class="title" @click="openDialog" @contextmenu.prevent="rightRemove && remove()">

                <template v-if="specified">
                    <slot>
                        <span class="id" v-if="hasId"> {{ model?.id || idModel }} </span>
                        <span class="label" v-if="hasLabel">{{ model?.label }}</span>
                    </slot>
                </template>

                <template v-else>
                    <slot name="null">
                        <Icon name="far-ban" />
                        <span> Unspecified</span>
                    </slot>
                </template>
            </div>
            <div class="btn" @click="remove" v-if="specified && removeButton">
                <Icon class="normal" name="far-times-circle" />
                <Icon class="highlight" name="fa-times-circle" />
            </div>
            <div class="btn" @click="openDialog" v-if="dialogButton">
                <Icon class="normal" name="far-arrow-alt-circle-right" />
                <Icon class="highlight" name="fas-arrow-alt-circle-right" />
            </div>
        </div>
    </span>
</template>

<style scoped lang="scss">
.quick-select {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    margin: 0;
    padding: 0;
    gap: 0 .5em;

    >li {
        display: flex;
        flex-direction: column;
        align-items: center;
        cursor: pointer;
        user-select: none;
        padding: 3px 8px;
        border: solid 1px transparent;
        border-radius: .5em;

        .images {
            display: inline-flex;
            flex-direction: row;
            align-items: stretch;
            list-style: none;
            margin: 0;
            padding: 0;
            // border: dashed 1px gray;
            background-color: #ffffee;

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
                    max-width: 2em;
                    max-height: 2em;
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

        .text {
            max-width: 6em;

            .icon {
                margin-right: .5em;
            }

            .id {
                display: none;

                &:before {
                    content: '[';
                }

                &:after {
                    content: '. ';
                    content: '] ';
                }
            }

            .label {
                // white-space: nowrap;
                vertical-align: top;
            }
        }

        &:hover {
            border: solid 1px lightgray;
            background-color: hsl(0, 0%, 97%);
        }

        &.selected {
            border: solid 1px hsl(300, 50%, 75%);
            background-color: hsl(300, 100%, 98%);
        }

        &.not-listed {
            border-style: dashed;
            border-width: 2px;
            padding: 2px 7px;

            .label:after {
                content: " *";
                vertical-align: top;
                font-size: 80%;
            }
        }
    }
}

.brief-entry {
    display: inline-flex;
    flex-direction: row;
    align-items: center;

    .title {
        border: solid 1px hsl(190, 30%, 45%);
        border-radius: .5rem;
        font-size: 80%;
        transform: scaleY(0.8);

        padding: .1rem .5rem;
        cursor: pointer;
        user-select: none;
        font-weight: 300;

        color: hsl(190, 30%, 30%);
        background-color: hsl(190, 30%, 92%);

        &:hover {
            background-color: hsl(140, 40%, 92%);
        }

        .id {
            display: inline-block;
            font-style: italic;
            transform: scaleX(70%);
            margin: 0 -10%;
            color: hsl(190, 40%, 50%);
        }

        .id+.label {
            &::before {
                content: " / ";
                color: #bbb;
            }
        }
    }

    &.unspecified .title {
        border-color: #888;
        color: #666;
        background-color: #eee;
        font-style: italic;
    }

    .btn {
        margin-left: 1em;
        font-size: 80%;
        color: hsl(220, 30%, 60%);
        user-select: none;
        cursor: pointer;

        &:hover .normal {
            display: none;
        }

        &:not(:hover) .highlight {
            display: none;
        }
    }

}
</style>
