
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoNodeTypeInfo from './CoNodeTypeInfo';
import CoCategoryValidators from './CoCategoryValidators';

export class CoCategoryTypeInfo extends CoNodeTypeInfo {
    
    static validators = new CoCategoryValidators();

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoCategoryTypeInfo.declaredProperty);
    }

}

export default CoCategoryTypeInfo;