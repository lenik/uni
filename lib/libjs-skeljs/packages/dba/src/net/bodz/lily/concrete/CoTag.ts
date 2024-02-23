import { integer } from '@skeljs/core/src/lang/type';
import { CoCode } from './CoCode';

export abstract class CoTag<This> extends CoCode<This> {
    constructor(o: any) {
        super(o);
    }
}

export default CoTag;