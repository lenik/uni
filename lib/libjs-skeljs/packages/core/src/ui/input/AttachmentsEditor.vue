<script lang="ts">
import { computed, inject, onMounted, ref } from "vue";
import { int } from "../../lang/basetype";
import { SERVER_URL } from "../../../../dba/src/ui/lily/context";

interface IAttachment {

}

export interface Props {
    serviceUrl?: string // injectable
    className: string
    id: any
    subDir?: string
    attachments: IAttachment[]
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

const rootElement = ref<HTMLElement>();

const serverUrl = inject<string>(SERVER_URL)!;
const realServiceUrl = computed(() => {
    let url;
    if (props.serviceUrl != null)
        url = props.serviceUrl;
    else if (serverUrl != null)
        url = serverUrl + "/upload";
    else
        return undefined;

    url += "?schema=" + subDir.value;
});

const uploadUrl = computed(() => {
    return realServiceUrl.value + "/tmp";
});

const subDir = computed(() => {
    let dir = props.className;
    if (props.subDir != null)
        if (dir == null)
            dir = props.subDir;
        else
            dir += "/" + props.subDir;
    return dir;
});

const fileClass = computed(() => subDir.value);

// methods

defineExpose({ update });

function update() {
}

function remove(i: int) {
    props.attachments.splice(i, 1);
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <input type="file" class="upload" :data-url="uploadUrl" data-list="attachments">
        <ul class="previews" v-if="attachments != null">
            <li v-for="(attachment, i) in attachments" :key="i">
                <img :src="getItemImage(attachment, id)">
                <div class="caption">{{ attachment.name }}
                    <Icon name="far-trash" @click="remove(i)" />
                </div>
            </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}
</style>
