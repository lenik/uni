import $ from 'jquery';
import { inject } from 'vue';

import { wireUp } from 'skel01-core/src/lang/json';
import { showError } from 'skel01-core/src/logging/api';

import EntityType from './EntityType';
import { SERVER_URL } from '../../../../ui/lily/context';

export function useEntityController(type: EntityType) {
    const serverUrl = inject<string>(SERVER_URL)!;
    return new EntityController(type, serverUrl);
}

export class EntityController {

    type: EntityType
    serverUrl: string

    constructor(type: EntityType, serverUrl: string) {
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

    async filter(criteria: any) {
        let filterUrl = this.listUrl;
        let options: any = {
            row: 'object',
        };
        Object.assign(options, criteria);

        let data = await $.ajax({
            url: filterUrl,
            data: options,
            method: "GET"
        });

        if (typeof (data.rows) != "object") {
            showError("Invalid response: " + data);
            return;
        }

        let rows = wireUp(data.rows);

        let list: any[] = [];
        for (let row of rows) {
            let obj = this.type.fromJson(row);
            list.push(obj);
        }
        return list;
    }

}

export default EntityController;
