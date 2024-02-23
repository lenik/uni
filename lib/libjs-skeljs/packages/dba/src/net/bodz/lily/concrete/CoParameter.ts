import { integer } from '@skeljs/core/src/lang/type';
import { CoCode } from './CoCode';

export abstract class CoParameter<This> extends CoCode<This> {
    constructor(o: any) {
        super(o);
    }
}

export default CoParameter;