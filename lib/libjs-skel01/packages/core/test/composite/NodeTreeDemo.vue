<script lang="ts">
import { computed, ref } from 'vue';

export const title = 'recursive node tree demo';
</script>

<script setup lang="ts">
import { UiNode } from '../../src/ui/ui-node';

import NodeTree from '../../src/ui/tree/NodeTree.vue';

const root: UiNode<any> = ['root', {
    c1: ['child 1'],
    c2: ['child 2',
        {
            c21: ['child 2-1'],
            c22: ['child 2-2'],
        }, {
            listStyle: 'decimal',
        }],
    c3: ['child 3',
        {
            c31: ['child 3-1'],
            c32: ['child 3-2'],
        }]
}];

</script>

<template>
    <node-tree :node="root" :max-depth="1">

        <template v-slot="{ path, value }">
            <p> {{ path }}: {{ value }} </p>
        </template>
    </node-tree>
</template>

<style lang="scss" scoped></style>
