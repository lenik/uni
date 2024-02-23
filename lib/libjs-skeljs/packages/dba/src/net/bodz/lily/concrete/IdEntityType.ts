
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoObjectType } from './CoObjectType';

export class IdEntityType extends CoObjectType {

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(IdEntityType.declaredProperty);
    }
}

export default IdEntityType;