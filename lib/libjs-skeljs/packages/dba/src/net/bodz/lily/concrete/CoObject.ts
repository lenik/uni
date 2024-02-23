import moment from 'moment';
import { Moment } from 'moment';

import { integer } from '@skeljs/core/src/lang/type';

import StructRow from './StructRow';
import CoObjectType from './CoObjectType';

export abstract class CoObject extends StructRow {
    static TYPE = new CoObjectType();

    // UI

    label?: string
    description?: string
    comment?: string
    image?: string
    imageAlt?: string

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
