import { integer } from '@skeljs/core/src/lang/type';
import CoCode from './CoCode';
import CoTagType from './CoTagType';

export abstract class CoTag<This> extends CoCode<This> {
    static TYPE = new CoTagType();
    
    constructor(o: any) {
        super(o);
    }
}

export default CoTag;