<script setup lang="ts">
import { computed } from "vue";

import * as C from "./Counters";

interface Props {
    types: C.CounterTypes
    values: C.CounterValues
}

const props = withDefaults(defineProps<Props>(), {
});

function compareCounterKey(k1, k2) {
    let types = props.types;
    let t1 = types[k1];
    let t2 = types[k2];
    if (t1 == undefined) return 1;
    if (t2 == undefined) return -1;

    let p1 = t1.priority || 0;
    let p2 = t2.priority || 0;
    let cmp = p1 - p2;
    if (cmp != 0)
        return cmp;
    
    cmp = k1.localeCompare(k2);
    return cmp;
}

const results = computed(() => {
    let types = props.types;
    let values = props.values;
    let array: C.CounterTypedValue[] = [];
    let keys = Object.keys(types);
    keys.sort(compareCounterKey);
    for (let k of keys) {
        let type = types[k];
        let s;
        if (type.cformat != undefined) {
            s = type.cformat(values);
        } else {
            let val = values[k];
            if (type.format != undefined) {
                s = type.format(val);
            } else {
                s = val;
            }
        }
        if (s == undefined) continue;
        array.push({
            ...type, value: s
        });
    }
    return array;
});

function faClasses(type: C.CounterType) {
    let array: string[] = [];
    if (type.iconfa != null) {
        array.push('fa');
        array.push('fa-' + type.iconfa);
    }
    return array;
}
</script>

<template>
    <ul class="inline-block">
        <li class="counter-pair" v-for="(item, k) in results" :key="k">
            <span class="name">
                <i :class="faClasses(item)"></i>
                {{ item.label }}</span>
            <span class="val">{{ item.value }}</span>
        </li>
    </ul>
</template>

<style lang="scss" scoped>
.counter-pair {
    border: solid 1px gray;
    border-radius: 4px;
    margin: 0 .5em;
    font-family: Sans;
    font-size: 85%;
    vertical-align: top;
    display: flex;

    >* {
        vertical-align: top;
        display: inline-block;
        padding: .1em .4em;
    }

    .name {
        background: #eee;
        border-right: solid 1px gray;
        border-radius: 4px 0 0 4px;
        cursor: pointer;

        &:hover {
            background: #ddd;
        }

        i.fa {
            margin: 0 .2em;
        }
    }

    .val {}
}
</style>