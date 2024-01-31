import type { Api, Config } from "datatables.net";

import { simpleName } from "@skeljs/core/src/logging/api";
import { row2Obj, obj2Row, row2ObjSimple } from './objconv';

export interface DataTab {
    columns: DataTabColumn[]
    data: any[][]
}

export interface DataTabColumn {
    title: string
}

export type RenderFunc
    = (data: string | null, type: string, row: any[], meta: any) => void;
export type FormatFunc
    = (data: any) => string;
export type OnCellCreateFunc
    = (cell: HTMLElement, cellData: any, rowData: any, row: number, col: number) => void;

export type SymbolCompileFunc
    = (code: string) => any;

export type SetupDataFunc
    = (config: Config, columns: ColumnType[]) => any;

export type OnAppliedFunc
    = (dt: Api<any>, table: HTMLTableElement, setup: any) => void;

export type ConfigFn = (table: HTMLTableElement, config: Config) => void;

export interface TextMap {
    [k: string]: string
}

export interface ColumnType {
    position: number       // [?]

    field: string           // data-field
    params?: TextMap
    type?: string           // data-type
    format?: string         // data-format
    formatter?: FormatFunc   // override format

    ascending?: boolean     // data-order = asc(true) | desc(false) | undefined
    priority?: number       // data-order-priority

    render?: RenderFunc
    oncreate?: OnCellCreateFunc

    styleClass?: string

    icon?: string
    label?: string          // default to `field`
    description?: string
    tooltip?: string        // default to `description`
}

type Script = string;
interface _ThProps {
    dateField?: string
    dateType?: string
    dataPriority?: number
    dataOrder?: 'a' | 'asc' | 'ascend' | 'd' | 'desc' | 'descend' | true | false | 0 | 1 | undefined;
    dataClass?: string              // styleClass
    oncreate?: Script
    dataParam?: string              // k1=v1;k2=v2;...
    // [key: "param:*"]: string // => dataParam
    dataFormat?: string | Script
    dataRender?: string | Script    // also <script>
}

// spec

export function parseSpecParams(s: string): any {
    if (s == null)
        return undefined;
    let map: any = {};
    s.split(/;/g).forEach((g: string) => {
        let key = g = g.trim();
        let val: string | undefined = undefined;
        let eq = g.indexOf('=');
        if (eq != -1) {
            key = g.substring(0, eq).trim();
            val = g.substring(eq + 1).trim();
        }
        map[key] = val;
    });
    return map;
}

export function parseOrder(s: string): boolean | undefined {
    switch (s) {
        case 'a':
        case 'asc':
        case 'ascend':
        case 'true':
        case '1':
            return true;
        case 'd':
        case 'desc':
        case 'descend':
        case 'false':
        case '0':
            return false;
        default:
            return undefined;
    }
}

export function compileOnCreate(js?: string): OnCellCreateFunc | undefined {
    if (js == null)
        return undefined;
    let enclosed = "(function (cell, cellData, rowData, row, col) { " + js.trim() + " })";
    let f = eval(enclosed);
    return f;
}

export function compileRender(js: string): RenderFunc | undefined {
    if (js == null)
        return undefined;
    let enclosed = "(function (data, type, row, meta) { " + js.trim() + " })";
    let f = eval(enclosed);
    return f;
}


export class Selection {
    // select: boolean // true for select, false for unselect

    columns: ColumnType[]
    rows: any[][]
    dtIndexes: number[]

    constructor(columns: ColumnType[], rows: any[][] = [], dtIndexes: number[] = []) {
        this.columns = columns;
        this.rows = rows;
        this.dtIndexes = dtIndexes;
    }

    get firstRow() {
        return this.rows[0];
    }

    get firstDtIndex() {
        return this.dtIndexes[0];
    }

    get size() {
        return this.rows.length;
    }

    get empty() {
        return this.rows.length == 0;
    }

    toObject(index: number = 0, columns?: ColumnType[]) {
        let row = this.rows[index];
        if (row == null)
            return {};
        if (columns == null)
            columns = this.columns;
        return row2Obj(row, columns);
    }
}

// REFLECTION

export type integer = number;
export type long = number;

export interface IEntityType {
    name: string        // Java class name
    icon?: string

    label?: string
    description?: string

    property: EntityPropertyMap
}

export class EntityType implements IEntityType {
    name: string        // Java class name
    icon?: string

    label?: string
    description?: string

    property: EntityPropertyMap = {}

    get simpleName() {
        return simpleName(this.name);
    }
}

export interface EntityPropertyMap {
    [propertyName: string]: EntityProperty
}

export interface IEntityProperty {

    name?: string
    type: string // ts type, not java type
    // javaType: string
    precision?: number
    scale?: number
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

}

export class EntityProperty implements IEntityProperty {

    name?: string
    type: string
    precision?: number
    scale?: number
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    constructor(o: IEntityProperty) {
        Object.assign(this, o);
        // this.name = o.name;
        // this.type = o.type;
        // this.precision = o.precision;
        // this.scale = o.scale;
        // this.nullable = o.nullable != null ? o.nullable : true;
        // this.icon = o.icon;
        // this.label = o.label;
        // this.description = o.description;
    }

}

export class CoObject {
    label?: string
    description?: string
    comment?: string
    image?: string
    imageAlt?: string
    flags: integer
    priority: integer
    state: integer
    ownerUser: any
    ownerUserId: integer
    ownerGroup: any
    owenrGroupId: integer
    acl: integer
    accessMode: integer

    constructor(o: any) {
        if (o != null) Object.assign(this, o);
    }
}

export class IdEntity<Id> extends CoObject {
    id?: Id

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}
