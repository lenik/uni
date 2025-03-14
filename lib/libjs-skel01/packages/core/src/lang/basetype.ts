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

export type OpenInt = int | 'unlimit';
export type OpenLong = long | 'unlimit';

export type Array<E> = E[];
export type List<E> = E[];
// export type _Set<E> = Set<E>;
// export type _Map<K, V> = Map<K, V>;

export type Enum = string;

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

export function parseOptInt(s: string): int | undefined {
    switch (s) {
        case 'undefined':
        case '':
            return undefined;
        default:
            return parseInt(s);
    }
}

export function parseOptOpenInt(s: string): OpenInt | undefined {
    return s == 'unlimit' ? 'unlimit' : parseOptInt(s);
}

export function parseOptLong(s: string): long | undefined {
    switch (s) {
        case 'undefined':
        case '':
            return undefined;
        default:
            return parseInt(s);
    }
}

export function parseOptOpenLong(s: string): OpenLong | undefined {
    return s == 'unlimit' ? 'unlimit' : parseOptLong(s);
}

export function parseOptBoolean(s: string): boolean | undefined {
    switch (s) {
        case 'undefined':
        case '':
            return undefined;
        case 'true':
        case 'yes':
        case '1':
            return true;
        case 'false':
        case 'no':
        case '0':
            return false;
        default:
            throw new Error('error parse boolean: ' + s);
    }
}

export function parseOptBooleanOrAuto(s: string): boolean | 'auto' | undefined {
    switch (s) {
        case 'undefined':
        case '':
            return undefined;
        case 'auto':
            return 'auto';
        case 'true':
        case 'yes':
        case '1':
            return true;
        case 'false':
        case 'no':
        case '0':
            return false;
        default:
            throw new Error('error parse boolean: ' + s);
    }
}

export function parseOptString(s: string): string | undefined {
    switch (s) {
        case '':
            return undefined;
        default:
            return s;
    }
}
