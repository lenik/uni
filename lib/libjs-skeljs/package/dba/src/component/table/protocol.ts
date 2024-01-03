// url = ...
// protocol = lily

import { baseName } from "skeljs-core/src/io/url";

export interface DataTab {
    columns: DataTabColumn[]
    data: any[][]
}

export interface DataTabColumn {
    title: string
}

export interface FieldInfo {
    name: string        // data-field
    type: string        // data-type
    format: string      // data-format
}

export interface DataList {
    columns: string[]
    rows: any[]
}

export interface DataRow {
    [index: number]: any
}

export class AjaxProtocol {

    _url: string

    constructor(url: string) {
        this._url = url;
    }

    get url() {
        return this._url;
    }

    get method() {
        return 'POST';
    }

    getParameters(fields: FieldInfo[]) {
        return {};
    }

    toRowArray(data: any): DataRow[] {
        return data.data;
    }

}

export class Lily extends AjaxProtocol {

    entityClass: string     // simple name
    addClassColumn: boolean

    constructor(controllerUrl: string) {
        super(controllerUrl + "/__data__" + baseName(controllerUrl));
        this.entityClass = baseName(controllerUrl);
        this.addClassColumn = true;
    }

    getParameters(fields: FieldInfo[]) {
        let names = fields.map(f => f.name).join(", ");
        let formats = fields.map(f => f.format);
        return {
            row: "array",
            columns: names,
            formats: JSON.stringify(formats)
        }
    }

    toRowArray(_data: any): DataRow[] {
        let data: DataList = _data;
        if (data == null)
            throw "null response";
        if (! Array.isArray(data.rows))
            throw "no rows array in the reponse data";
        if (this.addClassColumn) {
            if (data.rows.length > 0) {
                let firstRow = data.rows[0];
                if (Array.isArray(firstRow)) {
                    data.columns.push('_class');
                    data.rows.forEach(row => { row.push(this.entityClass); });
                } else if (typeof 'firstRow' == 'object') {
                    data.rows.forEach(row => {
                        row._class = this.entityClass;
                    });
                }
            }
        }
        return data.rows;
    }

}
