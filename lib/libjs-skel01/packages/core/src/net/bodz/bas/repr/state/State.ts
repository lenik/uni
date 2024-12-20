import { int } from '../../../../../lang/basetype';
import Predef from '../../../../../lang/Predef';

import StateType from './StateType';

export class State extends Predef<int>{

    _type: StateType

    constructor(key: int, name: string, type: StateType,
        label?: string, icon?: string, description?: string) {
        super(key, name, label, icon, description);
        this._type = type;
    }

    get type() { return this._type; }

}

export default State;
