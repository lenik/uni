import { integer } from '@skeljs/core/src/lang/type';
import CoNode from './CoNode';
import CoCategoryType from './CoCategoryType';

export abstract class CoCategory<This, Id> extends CoNode<This, Id> {
    static TYPE = new CoCategoryType();
    
    properties: any
    
    constructor(o: any) {
        super(o);
    }
}

export default CoCategory;