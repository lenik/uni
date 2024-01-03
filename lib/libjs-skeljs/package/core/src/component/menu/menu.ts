
export interface MenuItem {
    priotity?: number
    label: string
    iconfa?: string
    checked?: boolean
    selected?: boolean
}

export interface Menu {
    [key: string]: MenuItem
}

export type ViewChangedListener = (newView: string, oldView: string) => void;

export function getSelection(menu: Menu) {
    return Object.entries(menu).filter(ent => ent[1].selected).map(ent => ent[0]);
}

function compareMenuItem(a: MenuItem, b: MenuItem) {
    let p1: number = a.priotity || 0;
    let p2: number = a.priotity || 0;
    let cmp = p1 - p2;
    if (cmp != 0) return cmp;
    
    cmp = a.label.localeCompare(b.label);
    if (cmp != 0) return cmp;

    return -1;
}

export function sortedItems(menu: Menu): MenuItem[] {
    let v: MenuItem[] = Object.values(menu);
    v.sort(compareMenuItem);
    return v;
}
