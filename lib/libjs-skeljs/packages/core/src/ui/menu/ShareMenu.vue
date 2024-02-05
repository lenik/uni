<script lang="ts">
import { computed, onMounted, ref } from "vue";

import Icon from '../Icon.vue';
import Dialog from '../Dialog.vue';

export interface Props {
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

var iframeDialog = ref<InstanceType<typeof Dialog> | undefined>();
var iframeElement = ref();

function openShareWindow(url: string) {
    let width = 570;
    let height = 570;
    let left = (screen.width - width) / 2;
    let top = (screen.height - height) / 2;

    let params = {
        menubar: 'no',
        toolbar: 'no',
        status: 'no',
        width: width,
        height: width,
        top: top,
        left: left,
    };
    let s = '';
    for (let k in params)
        s += k + '=' + params[k] + ',';
    window.open(url, "NewWindow", s);
}

function openShareDialog(url) {
    let iframe = iframeElement.value;
    iframe.src = url;
    let dialog = iframeDialog.value;
    dialog?.open();
}

const tweet = computed(() => {
    let ogDescription = $("meta[property='og:description']").attr("content");
    var tweet = encodeURIComponent(ogDescription || '');
    return tweet;
});

const pageUrl = computed(() => document.URL);
const title = computed(() => document.title);
const imageUrl = computed(() => null);

function buildUrl(url: string, params: any) {
    let first = !url.includes('?');
    let sb = url;
    for (let k in params) {
        let v = params[k];
        if (v == null) continue;
        let vEncoded = encodeURIComponent(v);
        sb += first ? '?' : '&';
        sb += k + '=' + vEncoded;
    }
    return sb;
}

const facebookUrl = computed(() => buildUrl("https://www.facebook.com/sharer.php",
    { u: pageUrl.value }));
const twitterUrl = computed(() => buildUrl("https://twitter.com/intent/tweet",
    { url: pageUrl.value, text: tweet.value }));
const linkedInUrl = computed(() => buildUrl("https://www.linkedin.com/shareArticle",
    { url: pageUrl.value, mini: true, }));
const lineUrl = computed(() => buildUrl("https://social-plugins.line.me/lineit/share",
    { url: pageUrl.value, text: tweet, }));
const weiboUrl = computed(() => buildUrl("https://service.weibo.com/share/share.php",
    { url: pageUrl.value, title: title.value, pic: imageUrl }));

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="share-menu">
        <ul>
            <li @click="openShareWindow(facebookUrl)">
                <Icon name="fab-facebook" />
                <span>Facebook</span>
            </li>
            <li @click="openShareWindow(linkedInUrl)">
                <Icon name="fab-linkedin" />
                <span>LinkedIn</span>
            </li>
            <li @click="openShareWindow(lineUrl)">
                <Icon name="fab-line" />
                <span>Line</span>
            </li>
            <li @click="openShareWindow(twitterUrl)">
                <Icon name="fab-twitter" />
                <span>Twitter</span>
            </li>
            <li @click="openShareDialog(weiboUrl)">
                <Icon name="fab-weibo" />
                <span>新浪微博</span>
            </li>
        </ul>
        <Dialog class="iframe-dialog" ref="iframeDialog">
            <iframe ref="iframeElement" src="about:blank"></iframe>
        </Dialog>
    </div>
</template>

<style scoped lang="scss">
.share-menu {
    display: none;
    position: absolute;
    border: solid 1px hsl(180, 20%, 30%);
    // border-radius: 0 0 .5em .5em;
    padding: .4em .8em;
    right: .5em;
    //top: 1em;
    background-color: hsl(50, 35%, 90%); //, 80%);
    color: black;
    z-index: 1;
    font-size: 1rem;
    font-weight: 400;

    ul {
        margin: 0;
        padding: 0;
        display: flex;
        flex-direction: column;
        list-style: none;

        li {
            padding: .1em 0;
        }
    }

    .icon {
        color: hsl(100, 40%, 50%);
        margin-right: .6em;
    }
}

.iframe-dialog {

    ::v-deep(.dialog) {
        iframe {
            width: 600px;
            height: 400px;
        }

        .content {
            padding: 0;
        }
    }
}
</style>
