
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoObjectType } from './CoObjectType';

export class CoPrincipalType<Id> extends CoObjectType {

    static declaredProperty: EntityPropertyMap = {
        name: property({ type: 'string', precision: 30, }),
        properties: property({ type: 'any' }),
    };

    constructor() {
        super();
        this.declare(CoPrincipalType.declaredProperty);
    }
}

export default CoPrincipalType;