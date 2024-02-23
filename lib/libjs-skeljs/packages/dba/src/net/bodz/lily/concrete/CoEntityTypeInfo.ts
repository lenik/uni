
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoEntityValidators from './CoEntityValidators';
import CoObjectTypeInfo from './CoObjectTypeInfo';

export class CoEntityTypeInfo extends CoObjectTypeInfo {

    static validators = new CoEntityValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoEntityTypeInfo.declaredProperty);
    }

}

export default CoEntityTypeInfo;