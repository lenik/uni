
import { EntityPropertyMap, EntityType, property } from '../entity';

export class StructRowType extends EntityType {

    static declaredProperty: EntityPropertyMap = {
        creationDate: property({ type: 'date', icon: 'fa-tag' }),
        lastModifiedDate: property({ type: 'date', icon: 'fa-tag' }),
        version: property({ type: 'number', icon: 'fa-tag' }),
    };

    constructor() {
        super();
        this.declare(StructRowType.declaredProperty);
    }
}

export default StructRowType;