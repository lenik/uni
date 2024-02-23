import { integer } from '@skeljs/core/src/lang/type';
import CoCode from './CoCode';
import CoParameterTypeInfo from './CoParameterTypeInfo';

export abstract class CoParameter<This> extends CoCode<This> {
    static TYPE = new CoParameterTypeInfo();

    constructor(o: any) {
        super(o);
    }
}

export default CoParameter;