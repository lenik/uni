import { int } from "../../../../../lang/basetype";
import State from "./State";
import StateType from "./StateType";
import StateTypeInfo from "./StateTypeInfo";
import StateBuilder from "./StateBuilder";

const ID_INIT = 0;
const ID_OK = 1;
const ID_WAIT = 2;
const ID_USER = 100;
const ID_ERROR = -1;
const ID_EPSILON = -2;

export class DefaultState extends State {

    static _typeInfo: DefaultStateTypeInfo;
    static get TYPE() {
        if (this._typeInfo == null)
            this._typeInfo = new DefaultStateTypeInfo();
        return this._typeInfo;
    }


    constructor(key: int, name: string, type: StateType, label?: string, icon?: string, description?: string) {
        super(key, name, type, label, icon, description);
    }

    static nextId = 0;
    private static state(id: int) {
        if (id == 0)
            id = this.nextId++;
        return new StateBuilder().id(id);
    }

    static INIT = this.state(ID_INIT).name("init").nonterm().build();
    static OK = this.state(ID_OK).name("ok").accepted().build();
    static WAIT = this.state(ID_WAIT).name("wait").accepted().build();
    static ERROR = this.state(ID_ERROR).name("error").error().build();
    static EPSILON = this.state(ID_EPSILON).name("epsilon").nonterm().build();

}

export class DefaultStateTypeInfo extends StateTypeInfo {
    constructor() {
        super(DefaultState);
    }

}
export default DefaultState;
