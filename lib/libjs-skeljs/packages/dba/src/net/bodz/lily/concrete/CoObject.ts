import moment from 'moment';
import { Moment } from 'moment';

import { integer } from '@skeljs/core/src/lang/type';

import StructRow from './StructRow';
import CoObjectTypeInfo from './CoObjectTypeInfo';

export abstract class CoObject extends StructRow {
    static TYPE = new CoObjectTypeInfo();

    // UI

    label?: string
    description?: string
    icon?: string

    // state

    flags: integer = 0
    priority: integer = 0
    state: integer = 0

    // access control

    ownerUser: any
    ownerUserId: integer
    ownerGroup: any
    owenrGroupId: integer
    acl: integer
    accessMode: integer

    constructor(o: any) {
        super(o);
    }
}

export default CoObject;
