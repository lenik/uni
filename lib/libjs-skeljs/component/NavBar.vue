
<script setup lang="ts">

interface Item {
    label: string
    iconfa: string
}

interface Items {
    [key: string]: Item
}

const model = defineModel();
const props = defineProps<{
  items: Items
}>();

function select(k: string) {
    model.value = k;
}
</script>

<template>
    <nav class="-tabular -bw">
        <a v-for="(it, k) in items"
            :key="k"
            :href="k"
            :class="{ selected: modelValue == k }"
            onclick="return false"
            @click.stop="select(k)">
            <i :class="'fa fa-'+it.iconfa"></i>
            {{it.label}}</a>
    </nav>
</template>

<style lang="scss" scoped>

nav {
    white-space: nowrap;

    > a {
        display: inline-block;
        cursor: pointer;
    }

    &.-tabular {
        padding: .5em 2em 0;
        border-bottom-style: solid;
        border-bottom-width: 1px;

        > a {
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
        > a {
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
