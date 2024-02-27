
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoNodeTypeInfo from './CoNodeTypeInfo';
import CoCategoryValidators from './CoCategoryValidators';

export class CoCategoryTypeInfo extends CoNodeTypeInfo {

    name = "net.bodz.lily.concrete.CoCategory"
    icon = "fa-crow"
    label = "Concrete Category"
    description = "A category is a classification or grouping of similar items or concepts. It helps organize information and make it easier to find and understand. Categories can be broad or specific, depending on the context in which they are used. In terms of search engines, categories are often used to group search results into related topics or themes to help users find what they are looking for more easily."

    validators = new CoCategoryValidators();

    declaredProperty: EntityPropertyMap = {
        id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoCategoryTypeInfo;