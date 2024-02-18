import moment from 'moment';
import { Moment } from 'moment';

import { integer } from '@skeljs/core/src/lang/type';

export abstract class CoObject {

    // content

    creationDate: Moment = moment()
    lastModified: Moment = moment()

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
    version: integer = 0

    // access control

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

