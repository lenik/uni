import { integer } from '@skeljs/core/src/lang/type';
import IdEntity from './IdEntity';
import CoNodeType from './CoNodeType';

export abstract class CoNode<This, Id> extends IdEntity<Id> {
    static TYPE = new CoNodeType();

    parent?: This
    parentId?: Id

    children: This[]

    refCount?: integer
    
    constructor(o: any) {
        super(o);
    }
}

export default CoNode;