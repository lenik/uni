
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoObjectType } from './CoObjectType';

export abstract class CoPrincipalType<Id> extends CoObjectType {

    static declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoPrincipalType.declaredProperty);
    }
}

