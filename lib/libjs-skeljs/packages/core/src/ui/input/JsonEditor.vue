<script lang="ts">
import 'jsoneditor/dist/jsoneditor.css';
import 'jsoneditor/dist/jsoneditor.js';

import { onMounted, ref, watch } from "vue";
import { } from "../types";
import JSONEditor from 'jsoneditor';

type ModeType = 'tree' | 'view' | 'form' | 'code' | 'text' | 'preview';

export interface Props {
    expandAll?: boolean,
    parsed?: boolean,
    gutter?: boolean,

    className?: (e: { path: string[], field: string, value: string }) => string,
    editable?: (e: { path: string[], field: string, value: string }) => boolean,
    nodeName?: (e: { path: string[], type: 'object' | 'array', size: number, value: any }) => string | undefined
    validate?: (json: any) => any,
    createMenu?: (items: any, node: any) => any[],

    escapeUnicode?: boolean,
    sortObjectKeys?: boolean,
    limitDragging?: boolean,
    history?: boolean,
    mode?: ModeType,
    modes?: string[],
    name?: string,
    schema?: any,
    schemaRefs?: any,
    allowSchemaSuggestions?: boolean,
    search?: boolean,
    indentation?: number,
    theme?: string,
    templates?: any,
    autocomplete?: any,
    mainMenuBar?: boolean,
    navigationBar?: boolean,
    statusBar?: boolean,
    showErrorTable?: boolean | any[],
    // onTextSelectionChange
    // onSelectionChange
    // onEvent
    // onFocus
    // onBlur
    colorPicker?: boolean,
    // onColorPicker
    // timestampTag
    // timestampFormat
    language?: string,
    languages?: any,
    modalAnchor?: HTMLElement,
    popupAnchor?: HTMLElement,
    enableSort?: boolean,
    enableTransform?: boolean,
    maxVisibleChilds?: number,
    // createQuery
    // executeQuery
    queryDescription?: string,
}
</script>

<script setup lang="ts">
const model = defineModel<any>();

const props = withDefaults(defineProps<Props>(), {
    expandAll: true,
    parsed: true,
    gutter: false,

    escapeUnicode: undefined,
    sortObjectKeys: undefined,
    limitDragging: undefined,
    history: undefined,
    mode: 'code',
    modes: undefined,
    name: undefined,
    schema: undefined,
    schemaRefs: undefined,
    allowSchemaSuggestions: undefined,
    search: undefined,
    indentation: undefined,
    theme: undefined,
    templates: undefined,
    autocomplete: undefined,
    mainMenuBar: false,
    navigationBar: false,
    statusBar: false,
    showErrorTable: undefined,
    colorPicker: undefined,
    language: undefined,
    languages: undefined,
    modalAnchor: undefined,
    popupAnchor: undefined,
    enableSort: undefined,
    enableTransform: undefined,
    maxVisibleChilds: undefined,
    queryDescription: undefined,
});

const emit = defineEmits<{
    change: [],
    /** changes made by the user, not in case of programmatic changes via the functions set, setText, update, or updateText */
    changeJson: [json: any],
    /** changes made by the user, not in case of programmatic changes via the functions set, setText, update, or updateText */
    changeText: [json: string],
    expand: [{ path: string[], isExpand: boolean, recursive: boolean }],
    error: [error: any],
    modeChange: [newMode: any, oldMode: any],
    validationError: [errors: any[]], // ValidationError[]
}>();

// property shortcuts
const rootElement = ref<HTMLElement>();
const editor = ref();

let refresh = (val: any) => {
    let ed = editor.value;
    if (ed != null) {
        let json = val || model.value;
        ed.set(json);
    }
};
watch(model, (newVal) => refresh(newVal));

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
    let div = rootElement.value;
    let options: any = {
        onClassName: props.className,
        onEditable: props.editable,
        onNodeName: props.nodeName,
        onValidate: props.validate,
        onCreateMenu: props.createMenu,

        escapeUnicode: props.escapeUnicode,
        sortObjectKeys: props.sortObjectKeys,
        limitDragging: props.limitDragging,
        history: props.history,
        mode: props.mode,
        modes: props.modes,
        name: props.name,
        schema: props.schema,
        schemaRefs: props.schemaRefs,
        allowSchemaSuggestions: props.allowSchemaSuggestions,
        search: props.search,
        indentation: props.indentation,
        theme: props.theme,
        templates: props.templates,
        autocomplete: props.autocomplete,
        mainMenuBar: props.mainMenuBar,
        navigationBar: props.navigationBar,
        statusBar: props.statusBar,
        showErrorTable: props.showErrorTable,
        colorPicker: props.colorPicker,
        language: props.language,
        languages: props.languages,
        modalAnchor: props.modalAnchor,
        popupAnchor: props.popupAnchor,
        enableSort: props.enableSort,
        enableTransform: props.enableTransform,
        maxVisibleChilds: props.maxVisibleChilds,
        queryDescription: props.queryDescription,
    };
    for (let k in options)
        if (options[k] == null)
            delete options[k];

    options.onChange = () =>
        emit('change');
    if (props.mode == 'text' || props.mode == 'code') {
        options.onChangeText = (jsonText: string) => {
            if (props.parsed)
                try {
                    let parsed = JSON.parse(jsonText);
                    model.value = parsed;
                } catch (err) {
                }
            else
                model.value = jsonText;
            emit('changeText', jsonText);
        };
    } else {
        options.onChangeJSON = (json: any) => {
            model.value = json;
            emit('changeJson', json);
        };
    }
    options.onExpand = (e: { path: string[], isExpand: boolean, recursive: boolean }) =>
        emit('expand', e);
    options.onError = (error: any) =>
        emit('error', error);
    options.onModeChange = (newMode: any, oldMode: any) =>
        emit('modeChange', newMode, oldMode);
    options.onValidationError = (errors: any[]) =>
        emit('validationError', errors);

    let ed = new JSONEditor(div, options);
    refresh();

    let aceEditor = ed.aceEditor;
    let renderer = aceEditor.renderer;

    if (!props.gutter)
        renderer.setShowGutter(false);

    if (props.mode == 'tree')
        if (props.expandAll)
            ed.expandAll();

    editor.value = ed;
});

</script>

<template>
    <div class="jsoneditor-wrapper" ref="rootElement">
    </div>
</template>

<style scoped lang="scss">
.jsoneditor-wrapper {}
</style>
