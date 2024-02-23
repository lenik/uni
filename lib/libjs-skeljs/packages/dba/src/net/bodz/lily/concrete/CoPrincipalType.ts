import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoObjectType from './CoObjectType';
import CoPrincipalValidators from './CoPrincipalValidators';

export class CoPrincipalType extends CoObjectType {

    static validators = new CoPrincipalValidators();

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