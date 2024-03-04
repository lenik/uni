import Big from 'big.js';

export type byte = number;
export type short = number;
export type int = number;
export type long = number;
export type float = number;
export type double = number;
export type char = string;
export type _string = string;
export type _boolean = boolean;

export type _Date = Date;
export type SQLDate = Date;
export type Timestamp = Date;

export type BigInteger = bigint;
export type BigDecimal = Big;

export type Array<E> = E[];
// export type _List<E> = E[];
// export type _Set<E> = Set<E>;
// export type _Map<K, V> = Map<K, V>;

export type InetAddress = string;

export interface IExample {

    get name();
    get ordinal(): int;
    get icon(): string | undefined;
    get label(): string | undefined;
    get display(): string;
    get description(): string | undefined;

}

export class Example implements IExample {

    get name();
    get ordinal(): int;
    get icon(): string | undefined;
    get label(): string | undefined;
    get display(): string;
    get description(): string | undefined;

}

export interface ExamplesType<E extends IExample> {

    get size(): int;

    values(): E[];

    get(ordinal: int): E | undefined;

    forName(name: string): E | undefined;

}

export class Examples {
}

class Color extends Examples {

    static _ord = 0;
    static item(info: string | any) {
        let ord = this._ord++;
        let name;
        if (typeof info == 'string') {
            name = info;
        } else {
            name = info.name;
        }
    }
    static RED = this.item('red');
    static BLACK = this.item('black');

}
