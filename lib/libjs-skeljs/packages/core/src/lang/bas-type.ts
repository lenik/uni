import type { int } from "./basetype"

export type JsonVariant = any;

export interface IState {

    key: int
    name: string

}

export class State implements IState {

    key: int
    name: string

    constructor(key: int, name: string) {
        this.key = key;
        this.name = name;
    }

}
