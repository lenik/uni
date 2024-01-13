<script setup lang="ts">

import { ref } from 'vue';
import Icon from '../src/ui/Icon.vue';

var sets = [
    {
        type: 'font-awesome',
        naming: (n: string) => 'fa-' + n,
        list: [
            'tag', 'user', 'user-o',
        ]
    },
    {
        type: 'font-awesome 5.0 pro',
        naming: (n: string) => 'fa-' + n,
        list: [
            'sack-dollar',
            'chart-line',
        ]
    }
];

</script>

<template>
    <div v-for="set in sets" :key="set.type">
        <h2>
            Icon Type: {{ set.type }}
        </h2>

        <ul>
            <li class="item" v-for="(item, i) in set.list" :key="i">
                <Icon :name="set.naming(item)" />
                <span class="name">
                    {{ set.naming(item) }}
                </span>
            </li>
        </ul>
    </div>
</template>

<style lang="scss" scoped>
h2 {
    font-size: large;
    font-weight: 500;
}

* {}

ul {
    margin: 0;
    columns: 4;
}

.item {
    display: flex;
    flex-direction: row;
    // justify-content: center;
    align-items: center;
    margin: .5em 0;

    &:nth-child(even) {
        background: #fff8ff;
    }
}

.icon {
    display: inline-block;
    width: 3em;
    text-align: center;
    // border-bottom: dashed 1px gray;
    margin-right: .5em;

    &::before {
        content: ' ';
        border-right: solid 1px gray;
        margin-right: .5em;
    }

    &::after {
        content: ' ';
        border-left: solid 1px gray;
        margin-left: .5em;
    }
}

.name {
    font-weight: 300;
}
</style>
