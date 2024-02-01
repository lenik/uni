
import { EntityPropertyMap, EntityType, integer, primaryKey, property } from '../lily/entity';

export class CoObjectType extends EntityType {

    static declaredProperty: EntityPropertyMap = {
        label: property({ type: 'string', icon: 'fa-tag' }),
        description: property({ type: 'string', icon: 'far-info-circle' }),

        comment: property({ type: 'string', icon: 'fa-comment' }),
        image: property({ type: 'string', icon: 'fa-image' }),
        imageAlt: property({ type: 'string', icon: 'fa-tag' }),

        flags: property({ type: 'integer', icon: 'fa-tag' }),
        priority: property({ type: 'integer', icon: 'fa-tag' }),
        state: property({ type: 'integer', icon: 'fa-tag' }),

        ownerUser: property({ type: 'any', icon: 'fa-user' }),
        ownerGroup: property({ type: 'any', icon: 'fa-group' }),

        acl: property({ type: 'integer', icon: 'fa-tag' }),
        accessMode: property({ type: 'integer', icon: 'fa-tag' }),
    };

    constructor() {
        super();
        this.declare(CoObjectType.declaredProperty);
    }
}

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

export abstract class IdEntityType<Id> extends CoObjectType {

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(IdEntityType.declaredProperty);
    }
}

export abstract class IdEntity<Id> extends CoObject {
    id?: Id

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}
