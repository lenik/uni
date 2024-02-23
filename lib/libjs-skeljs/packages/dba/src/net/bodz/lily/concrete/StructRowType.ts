
import { EntityPropertyMap, EntityType, property } from '../entity';
import StructRowValidators from './StructRowValidators';

export class StructRowType extends EntityType {

    static validators = new StructRowValidators();

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