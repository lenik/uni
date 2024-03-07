import { int } from "../../../../../lang/basetype";
import State from "./State";
import StateType from "./StateType";

export class StateBuilder {

    _id: int
    _name: string
    _type: StateType

    id(id: int) {
        this._id = id;
        return this;
    }

    name(name: string) {
        this._name = name;
        return this;
    }

    type(type: StateType) {
        this._type = type;
        return this;
    }

    nonterm() {
        this._type = StateType.NONTERM;
        return this;
    }

    accepted() {
        this._type = StateType.ACCEPTED;
        return this;
    }

    error() {
        this._type = StateType.ERROR;
        return this;
    }

    build() {
        let state = new State(this._id, this._name, this._type);
        return state;
    }

}

export default StateBuilder;
