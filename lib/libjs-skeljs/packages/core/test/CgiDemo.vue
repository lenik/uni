<script setup lang="ts">

import { computed, ref } from 'vue';
import { VarMap } from '../src/lang/VarMap';

const queryString = ref('');
const varMap = computed(() => {
    // console.log(queryString.value);
    let varMap = VarMap.parse(queryString.value);
    // console.log(varMap.map);
    return varMap;
});

</script>

<template>
    <div>
        <textarea id="qs" cols="30" rows="10" v-model="queryString"></textarea>
    </div>
    <div> Parsed: </div>
    <ul v-if="varMap != null">
        <li v-for="name in varMap.keySet" :key="name">
            <span class="name"> {{ name }} </span>
            <ol>
                <li v-for="(item, i) in varMap.getArray(name) " :key="i">
                    <pre>{{ item }}</pre>
                </li>
            </ol>
        </li>
    </ul>
    <div v-if="varMap == null || varMap.empty">Empty.</div>
</template>

<style lang="scss" scoped></style>
