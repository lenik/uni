import { integer } from '@skeljs/core/src/lang/type';
import CoCode from './CoCode';
import CoPhaseType from './CoPhaseType';

export abstract class CoPhase<This> extends CoCode<This> {
    static TYPE = new CoPhaseType();

    constructor(o: any) {
        super(o);
    }
}

export default CoPhase;