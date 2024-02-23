
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoNodeType } from './CoNodeType';

export abstract class CoCategoryType extends CoNodeType {

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoCategoryType.declaredProperty);
    }
}

export default CoCategoryType;