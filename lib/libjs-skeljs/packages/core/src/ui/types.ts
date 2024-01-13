
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

        case null:
        case undefined:
            return false;
    }
    return false;
}

export type EventHandler = (event?: Event) => void | Promise<void>;
export type Href = string;

export interface Command {

    group?: string
    ordinal?: number

    pos?: 'left' | 'right'
    vPos?: 'top' | 'bottom'
    dir?: 'h' | 'v'

    name: string // unique name

    icon?: string
    label?: string
    description?: string
    tooltip?: string

    className?: string

    href?: string
    action?: 'close' | 'maximize' | 'toggle'
    run?: EventHandler

    sync?: boolean

}

export var dialogCmds = {
    close: {
        pos: 'right', name: 'close',
        icon: 'fa-close', label: 'Close',
        action: 'close',
        description: 'Close the dialog.',
        tooltip: 'Click on this button to close the current dialog.',
    }
};

export interface CommandGroup {
    name: string
    ordinalMin: number
    ordinalMax: number
    commands: Command[]
}

export type CommandGroupMap = { [group: string]: CommandGroup };

export function group(cmds: Command[], defaultGroup: string = 'default'): CommandGroupMap {
    let map: CommandGroupMap = {};
    for (let cmd of cmds) {
        let gName = cmd.group || defaultGroup;
        let g = map[gName];
        if (g == undefined) {
            g = {
                name: gName,
                commands: []
            }
            if (cmd.ordinal != null) {
                g.ordinalMin = cmd.ordinal;
                g.ordinalMax = cmd.ordinal;
            }
            map[gName] = g;
        } else {
            if (cmd.ordinal != null)
                if (g.ordinalMin == null) {
                    g.ordinalMin = cmd.ordinal;
                    g.ordinalMax = cmd.ordinal;
                } else {
                    if (cmd.ordinal < g.ordinalMin) g.ordinalMin = cmd.ordinal;
                    if (cmd.ordinal > g.ordinalMax) g.ordinalMax = cmd.ordinal;
                }
        }
        g.commands.push(cmd);
    }
    return map;
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
