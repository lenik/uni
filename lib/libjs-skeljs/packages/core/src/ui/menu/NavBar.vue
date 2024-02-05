<script setup lang="ts">

import $ from 'jquery';
import { ref, watch } from 'vue';
import type { ViewHandles } from '../layout/TabViews.vue';
import Icon from '../Icon.vue';

interface Props {
    items: ViewHandles
}

const model = defineModel<string>();
const viewData = defineModel<any>('data');

const props = defineProps<Props>();
const emit = defineEmits<{
    viewChanged: [newView: string, oldView?: string]
}>();

var rootElement = ref();

function select(newView: string) {
    const old = model.value;
    model.value = newView;
    emit('viewChanged', newView, old);
}

watch(model, async (toVal, fromVal) => {
    // console.log(`view changed from ${oldVal} to ${newVal}`);
    if (fromVal != null) {
        var $fromDiv = $("#v-" + fromVal);
        var onleave = $fromDiv.attr("onleave");
        if (onleave != null) eval(onleave);
    }

    var navLink = $(">[href=" + toVal + "]", rootElement);
    // navLink.siblings().removeClass("selected");
    // navLink.addClass("selected");

    var $toDiv = $("#v-" + toVal);
    // $toDiv.siblings().removeClass("selected");
    // $toDiv.addClass("selected");
    viewData.value = $toDiv.data();

    var onenter = $toDiv.attr("onenter");
    if (onenter != null) eval(onenter);

    return true;
});

</script>

<template>
    <nav ref="rootElement" class="-tabular -bw">
        <a v-for="(item, k) in items" :key="k" :href="'' + k" :class="{ selected: model == k }" onclick="return false"
            @click.stop="select('' + k)">
            <Icon :name="item.icon" v-if="item.icon != null" />
            <span class="label"> {{ item.label }} </span>
        </a>
    </nav>
</template>

<style lang="scss" scoped>
nav {
    white-space: nowrap;

    >a {
        display: inline-block;
        cursor: pointer;

        .icon {
            margin-right: .5em;
        }
    }

    &.-tabular {
        padding: .5em 2em 0;
        border-bottom-style: solid;
        border-bottom-width: 1px;

        >a {
            padding: .3em 1em;
            margin-bottom: -1px;
            border-style: solid;
            border-width: 1px;

            border-bottom-style: solid;
            border-bottom-width: 1px;
            border-radius: 4px 4px 0 0;
            font-family: Sans;
            // font-size: 110%;
            vertical-align: bottom;

            &:hover {
                border-style: solid;
                border-width: 1px;
            }

            &.selected {
                border-style: solid;
                border-width: 1px;
                border-top-style: solid;
                border-top-width: 2px;
                padding-bottom: .5em;
            }
        }
    }

    &.-bw {
        background: #fafaff;
        border-bottom-color: gray;

        >a {
            border-color: rgba(0, 0, 0, 0);
            border-bottom-color: gray;

            &:hover {
                background: #fff;
                border-color: gray;
                border-bottom-color: #fff;
            }

            &.selected {
                background: #fff;
                border-color: gray;
                border-top-color: #e96;
                border-bottom-color: #fff;
            }
        }
    }

}
</style>
