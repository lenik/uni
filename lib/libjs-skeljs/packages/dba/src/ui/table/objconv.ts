
import * as types from './types';

interface KeyOccurs {
    [key: string]: number
}

export function objv2Tab(objv: any[], fallback = null) {
    let colstat: KeyOccurs = {};
    for (let i = 0; i < objv.length; i++) {
        let obj = objv[i];
        if (obj == null) continue;
        for (let k in obj) {
            colstat[k] ||= 0;
            colstat[k]++;
        }
    }
    let keys = Object.keys(colstat);
    let columns = keys.map((c) => ({ title: c }));
    let data: any[] = [];
    for (let i = 0; i < objv.length; i++) {
        let obj = objv[i];
        let row: any[] = [];
        for (let ci = 0; ci < keys.length; ci++) {
            let k = keys[ci];
            let val = obj[k] || fallback;
            row.push(val);
        }
        data.push(row);
    }
    return {
        columns: columns,
        data: data
    };
}

export function tab2Objv(tab: types.DataTab) {
    let keys = tab.columns.map(c => c.title);
    let objv: any[] = [];
    for (let i = 0; i < tab.data.length; i++) {
        let row = tab.data[i];
        let obj: any = {};
        for (let j = 0; j < keys.length; j++)
            obj[keys[j]] = row[j];
        objv.push(obj);
    }
    return objv;
}

export function row2Obj(row: any, columns: any[]) {
    let obj: any = {};
    for (let i = 0; i < columns.length; i++) {
        let col = columns[i];
        let key: string = col.title || col;
        obj[key] = row[i];
    }
    return obj;
}

interface VarMatch {
    value: any
    remaining?: string
}

function findDeepest(varComposite: string, varMap: any): VarMatch {
    varMap[varComposite]
    let endpos = varComposite.length;
    while (endpos != -1) {
        endpos = varComposite.lastIndexOf('.', endpos);
        let left = varComposite;
        let right = undefined;
        if (endpos != -1) {
            left = varComposite.substring(0, endpos);
            right = varComposite.substring(endpos + 1);
            endpos--;
        }
        if (varMap[left] !== undefined)
            return { value: varMap[left], remaining: right };
    }
    return { value: undefined, remaining: varComposite };
}

function resolvePropertyComposite(obj: any, propertyVec: string[]): any {
    if (propertyVec.length == 0)
        return obj;
    if (obj == null)
        return undefined;
    for (let property of propertyVec) {
        obj = obj[property];
        if (obj == null) // null or undefined.
            break;
    }
    return obj;
}

export function convertToDataRows(fields: string[], cols: any[], rows: any[],
    mapFn?: (val: any) => any /* optional */): any[][] {

    if (rows.length == 0)
        return [];

    const isArray = Array.isArray(rows[0]);
    if (mapFn === undefined) mapFn = fillUndefs;

    if (cols == null) {
        if (isArray)
            throw "array[] without cols definition.";
        let dataTab = objv2Tab(rows);
        cols = dataTab.columns;
    }

    // config.columns = dataObj.columns;
    let colIndexMap: any = {};
    for (let i = 0; i < cols.length; i++) {
        const col = cols[i];
        const key = col.title || col;
        colIndexMap[key] = i;
    }

    let projRows = rows.map(r => []);
    for (let field of fields) {
        let fieldv = field.split(/\./);
        let { value: colIndex, remaining } = findDeepest(field, colIndexMap);
        let tailv = remaining == null ? [] : remaining.split(/\./);

        for (let y = 0; y < rows.length; y++) {
            const _row = rows[y];
            let projRow: any[] = projRows[y];

            let obj = undefined;
            if (isArray) {
                if (colIndex != undefined) {
                    obj = _row[colIndex];
                    obj = resolvePropertyComposite(obj, tailv);
                }
            } else {
                obj = resolvePropertyComposite(_row, fieldv);
            }
            if (mapFn != null)
                obj = mapFn(obj);
            projRow.push(obj);
        }
    }
    return projRows;
}

export function fillUndefs(val: any) {
    if (val === undefined)
        return "<span class='undef'></span>";
    if (val === null)
        return "<span class='null'></span>";
    if (val === undefined)
        return null;
    return val;
}
