import moment from 'moment';
import { Moment } from 'moment';

import { integer } from '@skeljs/core/src/lang/type';
import StructRowTypeInfo from './StructRowTypeInfo';

export abstract class StructRow {
    static TYPE: any = new StructRowTypeInfo();

    // content

    creationDate: Moment = moment()
    lastModifiedDate: Moment = moment()
    version: integer = 0

    constructor(o: any) {
        if (o != null)
            Object.assign(this, o);
    }
}

export default StructRow;
