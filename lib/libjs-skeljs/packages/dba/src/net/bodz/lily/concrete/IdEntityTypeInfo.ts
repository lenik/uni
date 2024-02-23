import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoObjectTypeInfo from './CoObjectTypeInfo';
import IdEntityValidators from './IdEntityValidators';

export class IdEntityTypeInfo extends CoObjectTypeInfo {

    static validators = new IdEntityValidators();

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(IdEntityTypeInfo.declaredProperty);
    }

}

export default IdEntityTypeInfo;