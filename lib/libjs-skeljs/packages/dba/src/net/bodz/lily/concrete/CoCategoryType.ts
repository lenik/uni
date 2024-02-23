
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoNodeType from './CoNodeType';
import CoCategoryValidators from './CoCategoryValidators';

export class CoCategoryType extends CoNodeType {
    
    static validators = new CoCategoryValidators();

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoCategoryType.declaredProperty);
    }

}

export default CoCategoryType;