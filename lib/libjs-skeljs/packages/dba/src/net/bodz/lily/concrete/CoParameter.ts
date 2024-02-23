import { integer } from '@skeljs/core/src/lang/type';
import CoCode from './CoCode';
import CoParameterType from './CoParameterType';

export abstract class CoParameter<This> extends CoCode<This> {
    static TYPE = new CoParameterType();

    constructor(o: any) {
        super(o);
    }
}

export default CoParameter;