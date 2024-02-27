import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeTypeInfo from './CoCodeTypeInfo';
import CoPhaseValidators from './CoPhaseValidators';

export class CoPhaseTypeInfo extends CoCodeTypeInfo {

    name = "net.bodz.lily.concrete.CoPhase"
    icon = "fa-leaf"
    label = "Concrete Phase"
    description = "Definition of a phase type."

    validators = new CoPhaseValidators();

    declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoPhaseTypeInfo;