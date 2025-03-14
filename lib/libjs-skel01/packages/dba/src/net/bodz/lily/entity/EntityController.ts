import $ from 'jquery';
import { inject } from 'vue';

import { wireUp } from 'skel01-core/src/lang/json';
import { showError } from 'skel01-core/src/logging/api';

import IEntityType from './IEntityType';
import { SERVER_URL } from '../../../../ui/lily/context';
import { long, OpenLong } from 'skel01-core/src/lang/basetype';
import { row2Obj } from '../../../../ui/table/objconv';

export function useEntityController(type: IEntityType, serverUrl: string) {
    return new EntityController(type, serverUrl);
}

export class SelectionRange {
    mode: 'offset' | 'page' = 'offset';
    _offset?: long;
    _limit?: OpenLong;
    countLimit?: OpenLong;

    constructor(offset?: long, limit?: OpenLong) {
        this._offset = offset;
        this._limit = limit;
    }

    assign(o: SelectionRange) {
        this.mode = o.mode;
        this._offset = o._offset;
        this._limit = o._limit;
        this.countLimit = o.countLimit;
        return this;
    }

    get offset() {
        return this._offset;
    }
    set offset(n: long | undefined) {
        this._offset = n;
        this.mode = 'offset';
    }

    get limit() {
        return this._limit;
    }
    set limit(n: OpenLong | undefined) {
        this._limit = n;
        this.mode = 'offset';
    }

    get pageIndex() {
        if (this._offset == undefined)
            return undefined;
        if (this._limit == undefined || this._limit == 'unlimit')
            return 0;
        return this._offset / this._limit;
    }
    set pageIndex(n: long | undefined) {
        if (n == undefined) {
            this._offset = undefined;
            return;
        }
        if (this._limit == undefined || this._limit == 'unlimit')
            throw new Error('set page index before set page size.');
        this._offset = n / this._limit;
        this.mode = 'page';
    }

    get pageSize() {
        return this._limit;
    }
    set pageSize(n: OpenLong | undefined) {
        this._limit = n;
        this.mode = 'page';
    }

    get pageNumber() {
        return this.pageIndex == undefined ? undefined : (this.pageIndex + 1);
    }
    set pageNumber(n: long | undefined) {
        this.pageIndex = n == undefined ? undefined : (n - 1);
        this.mode = 'page';
    }

    toString() {
        let sb = "offset " + this._offset;
        if (this._limit != undefined)
            if (this._limit == 'unlimit')
                sb += " unlimit";
            else
                sb += " limit" + this._limit;
        if (this._limit != undefined && this._limit != 'unlimit') {
            // page # of # rows
            let pageSize = this._limit;
            let pageIndex = (this._offset || 0) / this._limit;
            sb += ", page " + pageIndex + " of " + pageSize + " rows";

            if (this.countLimit != undefined) {
                sb += ", count ";
                if (this.countLimit == 'unlimit')
                    sb += " unlimit";
                else
                    sb += " limit " + this.countLimit;
            }
        }
        return sb;
    }
}

interface DataTableColumn {
    title: string;
}

export class ColumnOrder {

    name: string;
    ascending: boolean;

    constructor(name: string, ascending = true) {
        this.name = name;
        this.ascending = ascending;
    }

    toString() {
        return this.name + ' ' + (this.ascending ? 'asc' : 'desc');
    }

    static parse(s: string) {
        // ~field, -field: descending
        if (s.startsWith('~') || s.startsWith('-'))
            return new ColumnOrder(s.substring(1), false);
        // field+: ascending
        if (s.endsWith('+'))
            return new ColumnOrder(s.substring(0, s.length - 1), true);
        // field-: descending
        if (s.endsWith('-'))
            return new ColumnOrder(s.substring(0, s.length - 1), false);

        let m = s.match(/^(\w+|["'].*["'])(?:\s+(a|asc|ascend|d|desc|descend))$/i);
        if (!m)
            throw new Error('illegal column-order spec: ' + s);
        let [name, ord] = m;
        let ascending = true;
        if (ord != undefined)
            switch (ord.toLowerCase()) {
                case 'd':
                case 'desc':
                case 'descend':
                    ascending = false;
                    break;
            }
        return new ColumnOrder(name, ascending);
    }

}

export class ColumnOrders extends Array<ColumnOrder> {

    toString(): string {
        return this.join(', ');
    }

    static parseOpt(s: string): ColumnOrders | undefined {
        if (s == undefined || s == '')
            return undefined;
        return ColumnOrders.parse(s);
    }

    static parse(s: string): ColumnOrders {
        let a = new ColumnOrders();
        for (let item of s.split(/,/)) {
            let order = ColumnOrder.parse(item.trim());
            a.push(order);
        }
        return a;
    }

}

export class SelectOptions extends SelectionRange {
    rowFormat?: 'array' | 'object' = 'array';
    forDataTable: boolean = false;
    columns?: string[] | DataTableColumn[]; // pathAccessorMap.keys
    orders?: ColumnOrders;
    counting: boolean = false; // wantTotalCount
    formats?: any;

    constructor(offset?: long, limit?: OpenLong) {
        super(offset, limit);
    }

    override assign(o: SelectOptions) {
        super.assign(o);
        this.rowFormat = o.rowFormat;
        this.forDataTable = o.forDataTable;
        this.columns = o.columns;
        this.orders = o.orders;
        this.counting = o.counting;
        this.formats = o.formats;
        return this;
    }

    get arrayRow() { return this.rowFormat == 'array'; }
    get objectRow() { return this.rowFormat == 'object'; }

    order(column: string, ascending = true) {
        if (this.orders == null)
            this.orders = [];
        let order = new ColumnOrder(column, ascending);
        this.orders.push(order);
        return this;
    }

    get wantTotalCount() { return this.counting; }
    set wantTotalCount(val: boolean) { this.counting = val; }
}

export class EntityController {

    type: IEntityType;
    serverUrl: string;

    constructor(type: IEntityType, serverUrl: string) {
        this.type = type;
        this.serverUrl = serverUrl;
    }

    get entityClass() {
        return this.type.name;
    }

    get entityUrl() {
        return this.serverUrl + "/" + this.type.simpleName;
    }

    get listUrl() {
        return this.entityUrl + '/__data_' + this.type.simpleName;
    }

    idPath(id: any) {
        if (typeof id == 'object') {
            if (Array.isArray(id)) {
                let idvec: any[] = id as any[];
                return idvec.join('/');
            } else {
                let idcomp = id;
                let path = '';
                for (let k in idcomp) {
                    let v = idcomp[k];
                    if (path.length) path += '/';
                    path += encodeURI(v);
                }
                return path;
            }
        }
        return String(id);
    }

    async select(id: any) {
        let selectUrl = this.entityUrl + "/" + this.idPath(id);
        let fetchOpts: any = {
            // maxDepth: 3
        };

        let data = await $.ajax({
            url: selectUrl,
            data: fetchOpts,
            method: "GET"
        });

        if (typeof (data) != "object") {
            showError("Invalid response: " + data);
            return;
        }

        let jo = wireUp(data);
        let obj = this.type.fromJson(jo);
        return obj;
    }

    async all(options?: SelectOptions) {
        return await this.filter({}, options);
    }

    async filter(criteria: any, options?: SelectOptions) {
        let opts = new SelectOptions();
        if (options != null) opts.assign(options);

        if (criteria != null)
            Object.assign(opts, criteria);

        let filterUrl = this.listUrl;
        let data = await $.ajax({
            url: filterUrl,
            data: options,
            method: "GET"
        });

        if (!Array.isArray(data.rows)) {
            let err = "Invalid response: " + data;
            showError(err);
            throw new Error(err);
        }

        let objv;
        if (opts.arrayRow) {
            objv = [];
            for (let row of data.rows) {
                let obj = row2Obj(row, data.columns);
                objv.push(obj);
            }
        }
        else
            objv = data.rows;

        for (let i = 0; i < objv.length; i++)
            objv[i] = wireUp(objv[i]);

        let list: any[] = [];
        for (let row of objv) {
            let entity = this.type.fromJson(row);
            list.push(entity);
        }
        data.list = list;
        return data;
    }

}

export default EntityController;
