<script setup lang="ts">

defineProps<{
}>();

</script>

<template>
</template>

<style lang="scss">
.dialog {
    display: none;
    position: fixed;
    top: 5em;
    bottom: 5em;
    left: 10em;
    right: 10em;
    min-width: 20em;
    min-height: 10em;

    background: #fff;
    border: solid 5px hsla(200, 30%, 30%, .5);
    border-radius: 5px;
    box-shadow: 3px 3px 10px 0px gray;
    overflow: hidden;

    iframe {
        border: 0;
    }

    >.flex-column {
        height: 100%;

        .title {
            flex: 0 0 auto;
        }

        .cmds {
            flex: 0 0 auto;
        }

        .content {
            flex: 1;
        }
    }

    .title {
        background: #e0eef0;
        border-bottom: solid 1px gray;
        border-radius: 5px 5px 0 0;
        padding: .5em;
        font-size: 110%;
        font-weight: bold;
        text-align: center;
    }

    .body {
        overflow: auto;
        padding: 1em;
        height: 100%;
    }

    .cmds {
        margin: 0;
        padding: .2em .5em;
        background: #fafbf3;
        border-top: solid 1px gray;
        justify-content: flex-end;
    }

}

.modal {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    cursor: wait;
    background: rgba(128, 128, 128, .6);
    animation: bgloop 1.3s infinite alternate ease-in-out;

    @at-root {
        @keyframes bgloop {
            0% {
                background: hsla(60, 30%, 30%, .3);
            }

            100% {
                background: hsla(60, 30%, 30%, .8);
            }
        }
    }

    .flex-row {
        align-items: center;
        height: 100%;
    }

    .content {
        width: 100%;
        text-align: center;
        font-size: 5em;
        font-family: Sans;
        font-weight: bold;
        color: #356;
    }
}</style>