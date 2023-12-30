<script setup lang="ts">

defineProps<{
}>();

</script>

<template>
    <ul class="label-inputs">
        <slot></slot>
    </ul>
</template>

<style lang="scss" scoped>
.label-inputs::v-deep() {
    >ul {
        list-style: none;
    }

    &:not(.-mixed)>ul>li,
    &.-mixed .entry {
        display: flex;
        margin: .5em 0;

        >.label,
        >label {
            display: inline-block;
            width: 10em;
            text-align: right;
            vertical-align: middle;

            &:after {
                content: ":";
            }
        }

        >.content,
        > :nth-child(2) {
            text-align: left;
            flex: 1;
        }

        > :not(:first-child) {
            margin-left: 8px;
        }

        > :last-child {
            margin-right: 16px;
        }

        input[type=text] {
            width: 100%;
            font-family: monospace;
        }
    }
}
</style>