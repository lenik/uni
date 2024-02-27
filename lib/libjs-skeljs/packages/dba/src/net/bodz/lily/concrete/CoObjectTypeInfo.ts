
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoObjectValidators from './CoObjectValidators';
import StructRowTypeInfo from './StructRowTypeInfo';

export class CoObjectTypeInfo extends StructRowTypeInfo {

    name = "net.bodz.lily.concrete.CoObject"
    icon = "far-gem"
    label = "Concrete Object"
    description = "A concrete object is a physical object that can be perceived through the senses, such as touch or sight. Examples of concrete objects include a table, a chair, a book, a car, or a tree. These objects have a tangible presence and can be easily identified and described."

    validators = new CoObjectValidators();

    declaredProperty: EntityPropertyMap = {
        id: property({ type: 'number', icon: 'far-key' }),
        name: property({ type: 'string', icon: 'far-id-card' }),
        properties: property({ type: 'any', icon: 'fab-pagelines' }),

        label: property({ type: 'string', icon: 'far-tag' }),
        description: property({ type: 'string', icon: 'far-sticky-note' }),
        icon: property({ type: 'string', icon: 'far-image' }),

        flags: property({ type: 'integer', icon: 'far-toggle-on' }),
        priority: property({ type: 'integer', icon: 'far-lightbulb' }),
        state: property({ type: 'string', icon: 'far-heart' }),

        ownerUser: property({ type: 'any', icon: 'far-user' }),
        ownerGroup: property({ type: 'any', icon: 'far-users' }),

        acl: property({ type: 'integer', icon: 'far-user-lock' }),
        accessMode: property({ type: 'integer', icon: 'far-key' }),
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoObjectTypeInfo;