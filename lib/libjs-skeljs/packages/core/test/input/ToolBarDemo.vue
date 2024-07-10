<script lang="ts">
import 'jquery-ui-themes/themes/le-frog/jquery-ui.css';
import { computed, ref } from 'vue';
import { Command } from '../../src/ui/types';

export const title = 'ToolBar/toolitem demo';
</script>

<script setup lang="ts">
import ToolBar from '../../src/ui/menu/ToolBar.vue';

const cmds = ref<Command[]>([
    {
        vPos: "top", pos: 'left', group: 'file', name: 'new',
        icon: 'fa-file', label: 'New',
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'open',
        icon: 'far-folder-open', label: 'Open',
    }, {
        vPos: "top", pos: 'left', group: 'file', name: 'save',
        icon: 'fa-save', label: 'Save',
    },

    {
        vPos: "top", pos: 'right', group: 'media', name: 'play',
        icon: 'fas-play-circle', label: 'Play',
    }, {
        vPos: "top", pos: 'right', group: 'media', name: 'pause',
        icon: 'fas-pause-circle', label: 'Pause',
    }, {
        vPos: "top", pos: 'right', group: 'media', name: 'stop',
        icon: 'fas-stop-circle', label: 'Stop',
    },

    {
        vPos: "bottom", pos: 'right', group: 'volume', name: 'volume-up',
        icon: 'fa-volume-up', label: 'Louder',
    }, {
        vPos: "bottom", pos: 'right', group: 'volume', name: 'volume-down',
        icon: 'fa-volume-down', label: 'Quieter',
    }, {
        vPos: "bottom", pos: 'right', group: 'volume', name: 'mute',
        icon: 'fa-volume-mute', label: 'Mute',
    },

    {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'dial',
        icon: 'fa-phone', label: 'Dial',
    }, {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'sms',
        icon: 'fa-sms', label: 'Send SMS',
    }, {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'comments',
        icon: 'fa-comment-alt', label: 'Comments',
    }, {
        vPos: "bottom", pos: 'left', group: 'msg', name: 'messages',
        icon: 'fa-comment-dots', label: 'Messenger',
    },

]);

</script>

<template>
    <div>
        <h2> Separated by pos/v-pos: </h2> Top: <div class="top-bar flex-row">
            <ToolBar :src="cmds" vpos="top" pos="left">
            </ToolBar>
            <div class="filler"></div>
            <ToolBar :src="cmds" vpos="top" pos="right">
            </ToolBar>
        </div> Bottom: <div class="bottom-bar flex-row">
            <ToolBar :src="cmds" vpos="bottom" pos="left">
            </ToolBar>
            <div class="filler"></div>
            <ToolBar :src="cmds" vpos="bottom" pos="right">
            </ToolBar>
        </div>
        <hr>
        <h2> Mixed together....</h2>
        <div class="bottom-bar flex-row smaller">
            <ToolBar :src="cmds" pos="left">
            </ToolBar>
            <div class="filler"></div>
            <ToolBar :src="cmds" pos="right" show-label="0" show-border="0">
            </ToolBar>
        </div>
    </div>
</template>

<style lang="scss">
.smaller {
    font-size: 80%;
}

.top-bar,
.bottom-bar {
    width: 100%;
    align-items: center;
}

.filler {
    flex: 1;
}
</style>

<style lang="scss" scoped></style>
