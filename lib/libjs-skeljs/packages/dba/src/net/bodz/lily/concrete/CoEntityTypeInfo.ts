import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoEntityValidators from './CoEntityValidators';
import CoObjectTypeInfo from './CoObjectTypeInfo';

export class CoEntityTypeInfo extends CoObjectTypeInfo {

    name = "net.bodz.lily.concrete.CoEntity"
    icon = "far-cube"
    label = "Concrete Entity"
    description = "An entity always has an identity."

    validators = new CoEntityValidators();

    declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoEntityTypeInfo;