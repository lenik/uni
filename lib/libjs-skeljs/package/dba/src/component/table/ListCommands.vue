<script setup lang="ts">

defineProps<{
}>();

</script>

<template>
</template>

<style lang="scss" scoped>
.list-cmds {
    .list {
        padding: 1em;
        flex: 1;
        height: 100%;
        box-sizing: border-box;
        overflow: auto;
    }

    .cmds {
        flex: 0 0 auto;
        overflow: auto;
        background: #eef;

        @at-root {
            .no-project .for-project {
                opacity: 0.5;
            }
        }

        font-family: Sans;

        .header {
            padding: 0 .3em;
            font-size: 120%;
        }

        ul.btns {
            padding: 0;

            li {
                list-style-type: none;

                .fa {
                    width: 1.2em;
                    text-align: right;
                }

                &:hover {
                    cursor: pointer;
                }
            }
        }
    }

    &.flex-row {
        flex: 1;
        height: 100%;

        .cmds {
            padding: 1em;
            border-left: solid 1px gray;

            .btns li {
                margin: .5em 0;
                padding-right: 4px;
                border-bottom: solid 1px rgba(255, 255, 255, 0);
                border-right: solid 3px rgba(255, 255, 255, 0);

                &:hover {
                    border-bottom: dashed 1px gray;
                    border-right: solid 3px gray;
                }
            }
        }
    }

    &.flex-column {
        flex: 1;
        width: 100%;
        height: 100%;

        .cmds {
            padding: .3em;
            border-top: solid 1px gray;
            text-align: right;

            ul {
                margin: 0;
            }

            li {
                display: inline-block;
                border: solid 1px rgba(255, 255, 255, 0);
                border-radius: 4px;
                padding: 4px 8px;

                &:hover {
                    border: solid 1px gray;
                    background: #eef;
                    box-shadow: 3px 3px 5px 0 rgba(100, 100, 100, .1);
                }
            }
        }
    }
}</style>