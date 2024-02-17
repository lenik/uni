
import { integer, primaryKey, property } from '../entity';

export abstract class CoObject {
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

