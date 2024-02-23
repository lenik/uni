
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoObjectType } from './CoObjectType';

export class CoEntityType extends CoObjectType {

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoEntityType.declaredProperty);
    }
}

export default CoEntityType;