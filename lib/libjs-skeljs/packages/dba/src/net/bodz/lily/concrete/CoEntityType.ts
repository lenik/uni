
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoEntityValidators from './CoEntityValidators';
import CoObjectType from './CoObjectType';

export class CoEntityType extends CoObjectType {

    static validators = new CoEntityValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoEntityType.declaredProperty);
    }

}

export default CoEntityType;