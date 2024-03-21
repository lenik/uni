import Big from 'big.js';
import { LocalDate, OffsetDateTime, ZonedDateTime } from './time';

export type byte = number;
export type short = number;
export type int = number;
export type long = number;
export type float = number;
export type double = number;
export type char = string;
export type _string = string;
export type _boolean = boolean;

export type BigInteger = bigint;
export type BigDecimal = Big;

export type Array<E> = E[];
export type List<E> = E[];
// export type _Set<E> = Set<E>;
// export type _Map<K, V> = Map<K, V>;

export type InetAddress = string;

export interface IExample {

    get name(): string;
    get ordinal(): int;
    get icon(): string | undefined;
    get label(): string | undefined;
    get display(): string;
    get description(): string | undefined;

}

export abstract class Example implements IExample {

    abstract get name(): string;
    abstract get ordinal(): int;
    abstract get icon(): string | undefined;
    abstract get label(): string | undefined;
    abstract get display(): string;
    abstract get description(): string | undefined;

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
