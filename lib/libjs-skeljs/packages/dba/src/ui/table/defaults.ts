import { Command, Status } from '@skeljs/core/src/ui/types';

export function getDefaultCommands(provides: any) {
    let cmds: Command[] = [];
    if (provides.reload != null)
        cmds.push({
            vPos: "top", pos: 'left', group: 'edit', name: 'reload',
            icon: 'fa-sync', label: 'Reload',
            run: () => provides.reload()
        });
    if (provides.openNew != null)
        cmds.push({
            vPos: "top", pos: 'left', group: 'file', name: 'new',
            icon: 'fa-file', label: 'New',
            run: provides.openNew
        });
    if (provides.openSelected != null)
        cmds.push({
            vPos: "top", pos: 'left', group: 'file', name: 'open',
            icon: 'far-folder-open', label: 'Open',
            run: provides.openSelected
        });
    if (provides.toggleMultiMode != null && provides.multiMode != null)
        cmds.push({
            vPos: "top", pos: 'right', group: 'edit', name: 'select',
            icon: 'fa-check', label: 'Multiple', type: 'toggle',
            run: provides.toggleMultiMode, checked: provides.multiMode
        });
    if (provides.toggleEditMode != null && provides.editMode != null)
        cmds.push({
            vPos: "top", pos: 'right', group: 'edit', name: 'edit',
            icon: 'fa-edit', label: 'Edit', type: 'toggle',
            run: provides.toggleEditMode, checked: provides.editMode
        });
    if (provides.deleteSelection != null)
        cmds.push({
            vPos: "top", pos: 'right', group: 'edit', name: 'delete',
            icon: 'fa-trash', label: 'Delete',
            run: provides.deleteSelection
        });
    if (provides.saveEdits != null)
        cmds.push({
            vPos: "top", pos: 'right', group: 'edit', name: 'save',
            icon: 'fa-save', label: 'Save',
            run: provides.saveEdits
        });
    return cmds;
}

export function getDefaultStatuses(provides: any) {
    let statuses: Status[] = [];
    if (provides.selectionText != null)
        statuses.push({
            pos: 'left', name: 'selection',
            icon: 'fa-angle-double-right', label: 'Selection:',
            message: provides.selectionText
        });
    if (provides.selectedRowCount != null)
        statuses.push({
            pos: 'right', name: 'nRow',
            icon: 'fa-hashtag', label: 'Row Count:',
            message: provides.selectedRowCount
        });
    if (provides.changeCount != null)
        statuses.push({
            pos: 'right', name: 'nChange',
            icon: 'fa-pen-nib', label: 'Changed Items:',
            message: provides.changeCount
        });
    if (provides.pressedKeyName != null)
        statuses.push({
            pos: 'right', name: 'key',
            icon: 'fas-keyboard', label: 'Key:',
            message: provides.pressedKeyName
        });
    if (provides.stateText != null)
        statuses.push({
            pos: 'right', name: 'state',
            icon: 'fab-asymmetrik', label: 'State:',
            message: provides.stateText
        });
    return statuses;
}
