
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { IdEntityType } from './IdEntityType';

export abstract class CoNodeType extends IdEntityType {

    static declaredProperty: EntityPropertyMap = {
        parent: property({ type: 'any', }),
        refCount: property({ type: 'integer', nullable: false, precision: 19 }),
        depth: property({ type: 'integer', precision: 19 }),
    };

    constructor() {
        super();
        this.declare(CoNodeType.declaredProperty);
    }
}

export default CoNodeType;