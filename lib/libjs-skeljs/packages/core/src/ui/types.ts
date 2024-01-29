import { Ref } from "vue";

export function bool(val: boolean | number | string | null | undefined) {
    switch (val) {
        case true:
        case 'true':
        case 1:
        case '1':
            return true;

        case false:
        case 'false':
        case 0:
        case '0':
            return false;

        case '':
            return true;

        case null:
        case undefined:
            return false;
    }
    return false;
}


// UiGroup - UiGroupItem


export interface UiGroupItem {
    group?: string
    ordinal?: number
}

export interface UiComponent extends UiGroupItem {

    pos?: 'left' | 'right'
    vPos?: 'top' | 'bottom'
    dir?: 'h' | 'v'

    name: string // unique name

    icon?: string
    label?: string
    description?: string
    tooltip?: string

    className?: string

}

export type NameSet = { [k: string]: boolean };

export interface Selector {

    includeGroupSet?: NameSet  // include only these groups
    excludeGroupSet?: NameSet // exclude these groups

    includeSet?: NameSet // include only these commands by name
    excludeSet?: NameSet // include only these commands by name

    pos?: 'left' | 'right' | 'left?' | 'right?'  //  include if the position matches
    vpos?: 'top' | 'bottom' | 'top?' | 'bottom?' // include if the vertical position matches

}

export function select<E extends UiComponent>(selector: Selector, array: UiComponent[]) {
    return array.filter(item => {
        if (selector.includeSet != null)
            if (!selector.includeSet[item.name])
                return false;
        if (selector.excludeSet != null)
            if (selector.excludeSet[item.name])
                return false;

        let g = item.group || 'default';
        if (selector.includeGroupSet != null)
            if (!selector.includeGroupSet[g])
                return false;
        if (selector.excludeGroupSet != null)
            if (selector.excludeGroupSet[g])
                return false;

        switch (selector.pos) {
            case 'left':
            case 'right':
                if (item.pos != selector.pos)
                    return false;
                break;
            case 'left?':
                if (item.pos != null && item.pos != 'left')
                    return false;
                break;
            case 'right?':
                if (item.pos != null && item.pos != 'right')
                    return false;
                break;
        }

        switch (selector.vpos) {
            case 'top':
            case 'bottom':
                if (item.vPos != selector.vpos)
                    return false;
                break;
            case 'top?':
                if (item.vPos != null && item.vPos != 'top')
                    return false;
                break;
            case 'bottom?':
                if (item.vPos != null && item.vPos != 'bottom')
                    return false;
                break;
        }
        return true;
    });
}

export interface UiGroup<E> {
    name: string
    ordinalMin?: number
    ordinalMax?: number
    items: E[]
}

// type _UiGroupMap<E> = { [group: string]: UiGroup<E> };

export class UiGroupMap<E> {

    map: { [group: string]: UiGroup<E> }

    constructor() {
        this.map = {};
    }

    get(name: string) {
        return this.map[name];
    }

    set(name: string, group: UiGroup<E>) {
        this.map[name] = group;
    }

    get sortedNames(): string[] {
        let groups = Object.values(this.map);
        let gg = groups.sort((a: UiGroup<any>, b: UiGroup<any>) => {
            if (a.ordinalMin != b.ordinalMin) {
                if (a.ordinalMin == null)
                    return 1;
                if (b.ordinalMin == null)
                    return -1;
                return a.ordinalMin - b.ordinalMin;
            }
            if (a.name < b.name)
                return -1;
            if (a.name > b.name)
                return 1;
            return 0;
        });
        let names = groups.map(g => g.name);
        return names;
    }

}

export function group<E extends UiGroupItem>(items: E[], defaultGroup: string = 'default'): UiGroupMap<E> {
    let map = new UiGroupMap<E>();
    for (let item of items) {
        let gName = item.group || defaultGroup;
        let group = map.get(gName);
        if (group == undefined) {
            group = {
                name: gName,
                items: [],
                ordinalMin: item.ordinal,
                ordinalMax: item.ordinal
            };
            map.set(gName, group);
        } else {
            if (item.ordinal != null)
                if (group.ordinalMin == null) {
                    group.ordinalMin = item.ordinal;
                    group.ordinalMax = item.ordinal;
                } else {
                    if (item.ordinal < group.ordinalMin) group.ordinalMin = item.ordinal;
                    if (item.ordinal > group.ordinalMax!) group.ordinalMax = item.ordinal;
                }
        }
        group.items.push(item);
    }
    return map;
}

// Command

export type DialogAction =
    'close'
    | 'select'
    | 'maximize'
    | 'minimize'
    | 'move'
    | 'resize'
    | 'attach'
    | 'detach'
    ;
export type EventHandler = (event?: Event, cmd?: Command) => void | Promise<void>;
export type Href = string;

export interface Command extends UiComponent {

    type?: undefined | 'button' | 'toggle'
    checked?: boolean | Ref<boolean>
    enabled?: boolean | Ref<boolean>

    href?: string
    action?: DialogAction
    run?: EventHandler

    sync?: boolean

}

const defaultDialogCmds = {
    ok: {
        pos: 'right', name: 'select',
        icon: 'fa-check', label: 'OK',
        action: 'select',
        description: 'Confirm and close.',
        tooltip: 'Click on this button to select the contents in the dialog.',
    },

    cancel: {
        pos: 'right', name: 'cancel',
        icon: 'fa-ban', label: 'Cancel',
        action: 'close',
        description: 'Cancel the dialog.',
        tooltip: 'Click on this button to cancel and close the current dialog.',
    },

    close: {
        pos: 'right', name: 'close',
        icon: 'fa-close', label: 'Close',
        action: 'close',
        description: 'Close the dialog.',
        tooltip: 'Click on this button to close the current dialog.',
    }
};

export type CommandBehavior = DialogAction | EventHandler | Href | boolean;

export interface CommandBehaviorMap {
    [name: string]: CommandBehavior
}

export function getDialogCmds(map: CommandBehaviorMap): Command[] {
    let cmds: Command[] = [];
    for (let name in map) {
        let def = defaultDialogCmds[name];
        if (def == null)
            throw "invalid dialog command name: " + name;
        let cmd = { ...def }; // copy

        let b = map[name];
        switch (typeof b) {
            case 'boolean':
                if (b)
                    break;
                else
                    continue;

            case 'function':
                cmd.run = b as EventHandler;
                break;

            case 'string':
                switch (b) {
                    case 'close':
                    case 'select':
                    case 'maximize':
                    case 'minimize':
                    case 'move':
                    case 'resize':
                    case 'attach':
                    case 'detach':
                        cmd.action = b;
                        break;
                    default:
                        cmd.href = b;
                }
                break;

            default:
                throw "invalid behavior: " + b;
        }
        cmds.push(cmd);
    }
    return cmds;
}

// Validation

export interface ValidateResult {
    error: boolean
    errorCode?: number
    type?: string
    message?: string
    help?: string
    target?: any
}

export type Validator
    = (val: any) => ValidateResult;


// Status

export type MessageFunc = () => string;

export interface Status extends UiComponent {

    message?: any | Ref<any>

}

