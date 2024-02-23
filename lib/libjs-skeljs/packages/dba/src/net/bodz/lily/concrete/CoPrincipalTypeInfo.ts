import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoObjectTypeInfo from './CoObjectTypeInfo';
import CoPrincipalValidators from './CoPrincipalValidators';

export class CoPrincipalTypeInfo extends CoObjectTypeInfo {

    static validators = new CoPrincipalValidators();

    static declaredProperty: EntityPropertyMap = {
        name: property({ type: 'string', precision: 30, }),
        properties: property({ type: 'any' }),
    };

    constructor() {
        super();
        this.declare(CoPrincipalTypeInfo.declaredProperty);
    }

}

export default CoPrincipalTypeInfo;