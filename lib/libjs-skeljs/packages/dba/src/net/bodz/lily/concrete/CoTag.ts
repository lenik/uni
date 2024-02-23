import { integer } from '@skeljs/core/src/lang/type';
import CoCode from './CoCode';
import CoTagTypeInfo from './CoTagTypeInfo';

export abstract class CoTag<This> extends CoCode<This> {
    static TYPE = new CoTagTypeInfo();
    
    constructor(o: any) {
        super(o);
    }
}

export default CoTag;