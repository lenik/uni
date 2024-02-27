
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoNodeValidators from './CoNodeValidators';
import IdEntityTypeInfo from './IdEntityTypeInfo';

export class CoNodeTypeInfo extends IdEntityTypeInfo {

    name = "net.bodz.lily.concrete.CoNode"
    icon = "fa-sitemap"
    label = "Concrete Node"
    description = "A node can have parent, hence multiple nodes forms a tree."

    validators = new CoNodeValidators();

    declaredProperty: EntityPropertyMap = {
        parent: property({ type: 'any', icon: "far-tree" }),
        refCount: property({ type: 'integer', nullable: false, precision: 19 }),
        depth: property({ type: 'integer', precision: 19 }),
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoNodeTypeInfo;