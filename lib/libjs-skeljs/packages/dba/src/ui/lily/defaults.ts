import { Command, Status, getDialogCmds } from '@skeljs/core/src/ui/types';

import { Props as FieldRowProps } from '@skeljs/core/src/ui/FieldRow.vue';

export function getDefaultCommands(provides: any) {
    let cmds: Command[] = [];
    for (let k in provides) {
        let cmd = getDefaultCommand(k, provides);
        if (cmd != null)
            cmds.push(cmd);
    }
    return cmds;
}

export function getDefaultCommand(k: string, provides: any): Command | undefined {
    switch (k) {
        case 'reload':
            return {
                vPos: "top", pos: 'left', group: 'edit', name: 'reload',
                icon: 'fa-sync', label: 'Reload',
                run: () => provides.reload()
            };
        case 'openNew':
            return {
                vPos: "top", pos: 'left', group: 'file', name: 'new',
                icon: 'fa-file', label: 'New',
                run: provides.openNew
            };
        case 'openSelected':
            return {
                vPos: "top", pos: 'left', group: 'file', name: 'open',
                icon: 'far-folder-open', label: 'Open',
                run: provides.openSelected
            };
        case 'toggleMultiMode':
            return {
                vPos: "top", pos: 'right', group: 'edit', name: 'select',
                icon: 'fa-check', label: 'Multiple', type: 'toggle',
                run: provides.toggleMultiMode, checked: provides.multiMode
            };
            break;
        case 'toggleEditMode':
            return {
                vPos: "top", pos: 'right', group: 'edit', name: 'edit',
                icon: 'fa-edit', label: 'Edit', type: 'toggle',
                run: provides.toggleEditMode, checked: provides.editMode
            }; break;
        case 'deleteSelection':
            return {
                vPos: "top", pos: 'right', group: 'edit', name: 'delete',
                icon: 'fa-trash', label: 'Delete',
                run: provides.deleteSelection
            };
        case 'saveEdits':
            return {
                vPos: "top", pos: 'right', group: 'edit', name: 'save',
                icon: 'fa-save', label: 'Save',
                run: provides.saveEdits
            };
    }
}

export function getDefaultStatuses(provides: any) {
    let statuses: Status[] = [];
    for (let k in provides) {
        let status = getDefaultStatus(k, provides);
        if (status != null)
            statuses.push(status);
    }
    return statuses;
}

export function getDefaultStatus(k: string, provides: any): Status | undefined {
    switch (k) {
        case 'selectionText':
            return {
                pos: 'left', name: 'selection',
                icon: 'fa-angle-double-right', label: 'Selection:',
                message: provides.selectionText
            };
        case 'selectedRowCount':
            return {
                pos: 'right', name: 'nRow',
                icon: 'fa-hashtag', label: 'Row Count:',
                message: provides.selectedRowCount
            };
        case 'changeCount':
            return {
                pos: 'right', name: 'nChange',
                icon: 'fa-pen-nib', label: 'Changed Items:',
                message: provides.changeCount
            };
        case 'pressedKeyName':
            return {
                pos: 'right', name: 'key',
                icon: 'fas-keyboard', label: 'Key:',
                message: provides.pressedKeyName
            };
        case 'stateText':
            return {
                pos: 'right', name: 'state',
                icon: 'fab-asymmetrik', label: 'State:',
                message: provides.stateText
            };
    }
}

export function getDefaultFieldRowProps(config: any, tagName = 'li'): Partial<FieldRowProps> {
    let base: Partial<FieldRowProps> = {
        tagName: tagName,
        align: 'top',
        alignLabel: 'left',
        labelWidth: '8em',
        description: '...',
        // required: true,
        // watch: false,
    };
    if (config != null)
        Object.assign(base, config);
    return base;
};
