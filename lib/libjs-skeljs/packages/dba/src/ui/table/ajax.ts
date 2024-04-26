
import { baseName } from "@skeljs/core/src/io/url";

import type { ColumnType } from "./types";

export class AjaxProtocol {

    _url: string

    constructor(url: string) {
        this._url = url;
    }

    get url() {
        return this._url;
    }

    getParameters(fields: ColumnType[]) {
        return {};
    }

    toRowArray(data: any): any[][] {
        return data.data;
    }

}
