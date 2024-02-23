
import { EntityPropertyMap, primaryKey, property } from '../entity';

import StructRowType from './StructRowType';

export class CoObjectType extends StructRowType {

    static declaredProperty: EntityPropertyMap = {
        label: property({ type: 'string', icon: 'fa-tag' }),
        description: property({ type: 'string', icon: 'far-info-circle' }),
        icon: property({ type: 'string', icon: 'fa-image' }),

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

export default CoObjectType;