
import { EntityPropertyMap, EntityType, primaryKey, property } from '../entity';

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

