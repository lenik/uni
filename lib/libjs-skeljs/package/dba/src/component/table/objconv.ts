
import * as types from './protocol';

interface KeyOccurs {
    [key: string]: number
}

export function objv2Tab(objv: any[]) {
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
    let columns = keys.map( (c) => ({ title: c}) );
    let data = [];
    for (let i = 0; i < objv.length; i++) {
        let obj = objv[i];
        let row = [];
        for (let ci = 0; ci < keys.length; ci++) {
            let k = keys[ci];
            let val = obj[k];
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
