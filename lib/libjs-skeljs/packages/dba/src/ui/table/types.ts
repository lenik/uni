import type { Api, Config } from "datatables.net";

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


export interface Selection {
    event: any
    dataTable: Api<any>
    select: boolean // true for select, false for unselect
    dataRows: any[][]
    dataRow: any[] | null // first for multi-select
    rowIndexes: number[]
    rowIndex: number
}


export interface EntityType {
    name: string        // Java class name
    icon?: string

    label?: string
    description?: string

    columns?: ColumnType[]

}
