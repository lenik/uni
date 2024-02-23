import { integer } from '@skeljs/core/src/lang/type';
import { CoNode } from './CoNode';

export abstract class CoCategory<This, Id> extends CoNode<This, Id> {
    
    properties: any
    
    constructor(o: any) {
        super(o);
    }
}

export default CoCategory;