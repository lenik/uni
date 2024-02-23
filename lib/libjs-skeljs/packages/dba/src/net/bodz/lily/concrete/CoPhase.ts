import { integer } from '@skeljs/core/src/lang/type';
import CoCode from './CoCode';
import CoPhaseTypeInfo from './CoPhaseTypeInfo';

export abstract class CoPhase<This> extends CoCode<This> {
    static TYPE = new CoPhaseTypeInfo();

    constructor(o: any) {
        super(o);
    }
}

export default CoPhase;