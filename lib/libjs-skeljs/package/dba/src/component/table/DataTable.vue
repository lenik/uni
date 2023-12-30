<script setup lang="ts">

defineProps<{
}>();


const css = [
    "table",
    "table-striped",
    "table-hover",
    "table-condensed",
    "dataTable",
    "table-responsive",
];

</script>

<template>
    <table class="datatable" :class="css">
        <slot>
            <thead>
            </thead>
        </slot>
        <tbody></tbody>
    </table>
</template>

<style lang="scss" scoped>
.dataTables_wrapper {
    .dataTable {

        tbody {
            tr {
                &:hover {
                    background-color: #dee;
                }

                &.selected {
                    background-color: #7aa !important;
                }
            }
        }

        td.with-image {
            img {
                max-width: 1.5em;
                max-height: 1.5em;
                object-fit: contain;
            }
        }
    }

    ul.pagination {
        font-family: Sans;

        li {
            display: inline-block;
            margin: 0 .3em;
        }
    }
}
</style>