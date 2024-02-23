
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoNodeValidators from './CoNodeValidators';
import IdEntityTypeInfo from './IdEntityTypeInfo';

export class CoNodeTypeInfo extends IdEntityTypeInfo {

    static validators = new CoNodeValidators();

    static declaredProperty: EntityPropertyMap = {
        parent: property({ type: 'any', }),
        refCount: property({ type: 'integer', nullable: false, precision: 19 }),
        depth: property({ type: 'integer', precision: 19 }),
    };

    constructor() {
        super();
        this.declare(CoNodeTypeInfo.declaredProperty);
    }

}

export default CoNodeTypeInfo;