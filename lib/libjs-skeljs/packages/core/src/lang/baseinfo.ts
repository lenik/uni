import Big from "big.js";
import {
    BigDecimal, BigInteger,
    InetAddress,
    byte, char, double, float, int, long, short,
} from "./basetype";
import TypeInfo from "./TypeInfo";

export abstract class NumberType<T> extends TypeInfo<T> {

    get icon() { return "far-hashtag"; }

}

export class ByteType extends NumberType<byte> {

    get name() { return "byte"; }

    parse(s: string): byte {
        let n = parseInt(s);
        let val = Math.floor(n) & 0xFF;
        return val;
    }

}

export class ShortType extends NumberType<short> {

    get name() { return "short"; }

    parse(s: string): byte {
        let n = parseInt(s);
        let val = Math.floor(n) & 0xFFFF;
        return val;
    }

}

export class IntType extends NumberType<int> {

    get name() { return "int"; }

    parse(s: string) {
        let n = parseInt(s);
        let val = Math.floor(n) & 0xFFFF_FFFF;
        return val;
    }

}

export class LongType extends NumberType<long> {

    get name() { return "long"; }
    get description() { return "long int"; }

    parse(s: string) {
        let n = parseInt(s);
        n = Math.floor(n) & 0xFFFF_FFFF_FFFF_FFFF;
        return n;
    }

}

export class FloatType extends NumberType<float> {

    get name() { return "float"; }

    parse(s: string) {
        let n = parseFloat(s);
        return n;
    }

}

export class DoubleType extends NumberType<double> {

    get name() { return "double"; }

    parse(s: string) {
        let n = parseFloat(s);
        return n;
    }

}

export class BigIntegerType extends NumberType<BigInteger> {

    get name() { return "BigInteger"; }

    parse(s: string): BigInteger {
        let b = BigInt(s);
        return b;
    }

}

export class BigDecimalType extends NumberType<BigDecimal> {

    get name() { return "BigDecimal"; }

    parse(s: string): BigDecimal {
        let b = Big(s);
        return b;
    }

}

export class BooleanType extends TypeInfo<boolean> {

    get name() { return "boolean"; }

    parse(s: string): boolean {
        switch (s) {
            case "true":
            case "yes":
                return true;
            case "false":
            case "no":
                return false;
        }
        let num = parseInt(s);
        if (num)
            return true;
        return false;
    }

}

export class CharType extends TypeInfo<char> {

    get name() { return "char"; }
    get icon() { return "fa-font"; }

    parse(s: string) {
        if (s.length)
            return s.charAt(0);
        else
            return '\0';
    }

}

export class StringType extends TypeInfo<string> {

    get name() { return "string"; }
    get icon() { return "fa-font"; }

    format(val: string): string {
        return val;
    }

    parse(s: string) {
        return s;
    }

}

export class EnumType extends TypeInfo<string> {

    get name() { return "enum"; }
    get icon() { return "fas-list-ol"; }

    format(val: string): string {
        return val;
    }

    parse(s: string) {
        return s;
    }

}

export class DateType extends TypeInfo<Date> {

    get name() { return "Date"; }
    get icon() { return "fa-clock"; }

    format(val: Date): string {
        return val.toISOString();
    }

    parse(s: string): Date {
        return new Date(s);
    }

}

export abstract class CollectionType<E = any, C = E[]> extends TypeInfo<C> {

    itemType: TypeInfo<E>

    constructor(itemType: TypeInfo<E>) {
        super();
        this.itemType = itemType;
    }

    get name() { return "Collection"; }
    get icon() { return "far-cube"; }

    abstract create(): C;
    abstract add(collection: C, item: E): boolean;
    abstract remove(collection: C, item: E): boolean;
    abstract toArray(collection: C): E[];

    format(val: C): string {
        let array = this.toArray(val);
        return JSON.stringify(array);
    }

    parse(s: string): C {
        let instance = this.create();
        let a = JSON.parse(s);
        if (!Array.isArray(a))
            throw 'not an array: ' + a;
        for (let i = 0; i < a.length; i++) {
            let item = a[i];
            let elm = item as E; // TODO
            this.add(instance, elm)
        }
        return instance;
    }

}

export class ArrayType<E> extends CollectionType<E, E[]> {

    constructor(itemType: TypeInfo<E>) {
        super(itemType);
    }

    get name() { return "Array"; }
    get icon() { return "far-cube"; }

    override create() {
        return [];
    }

    override add(array: E[], item: E): boolean {
        array.push(item);
        return true;
    }

    override remove(array: E[], item: E): boolean {
        let pos = array.indexOf(item);
        if (pos == -1)
            return false;
        array.splice(pos, 1);
        return true;
    }

    override toArray(array: E[]): E[] {
        return array;
    }

}

export class ListType<E> extends ArrayType<E> {

    constructor(itemType: TypeInfo<E>) {
        super(itemType);
    }

    get name() { return "List"; }
    get icon() { return "far-cube"; }

}

export class SetType<E> extends CollectionType<E, Set<E>> {

    get name() { return "Set"; }
    get icon() { return "far-cube"; }

    constructor(itemType: TypeInfo<E>) {
        super(itemType);
    }

    override create() {
        return new Set<E>();
    }

    override add(set: Set<E>, item: E): boolean {
        if (set.has(item))
            return false;
        set.add(item);
        return true;
    }

    override remove(set: Set<E>, item: E): boolean {
        return set.delete(item);
    }

    override toArray(set: Set<E>): E[] {
        return [...set];
    }

}

export type MapEntry<K, V> = [key: K, val: V];

export class MapType<K, V> extends TypeInfo<Map<K, V>> {

    keyType: TypeInfo<K>
    valueType: TypeInfo<V>

    constructor(keyType: TypeInfo<K>, valueType: TypeInfo<V>) {
        super();
        this.keyType = keyType;
        this.valueType = valueType;
    }

    get name() { return "Map"; }
    get icon() { return "far-cube"; }

    create() {
        let map = new Map<K, V>();
        return map;
    }

    toObject(map: Map<K, V>): any {
        let obj: any = {};
        map.keys().forEach(k => {
            let v = map.get(k);
            obj[k] = v;
        });
        return obj;
    }

    format(val: Map<K, V>): string {
        let obj = this.toObject(val);
        return JSON.stringify(obj);
    }

    parse(s: string): Map<K, V> {
        let instance = this.create();
        let obj = JSON.parse(s);
        if (Array.isArray(obj))
            throw 'not an object: ' + obj;
        for (let k in obj) {
            let v = obj[k];
            let key = k as K; // TODO
            let value = v as V; // TODO
            instance.set(key, value);
        }
        return instance;
    }

}

export class InetAddressType extends TypeInfo<InetAddress> {

    get name() { return "InetAddress"; }
    get icon() { return "far-globe"; }

    format(val: InetAddress): string {
        return val;
    }

    parse(s: string): InetAddress {
        return s;
    }

}

export const BYTE = new ByteType();
export const SHORT = new ShortType();
export const INT = new IntType();
export const LONG = new LongType();
export const FLOAT = new FloatType();
export const DOUBLE = new DoubleType();
export const BIG_INTEGER = new BigIntegerType();
export const BIG_DECIMAL = new BigDecimalType();

export const BOOLEAN = new BooleanType();
export const CHAR = new CharType();
export const STRING = new StringType();

export const ENUM = new EnumType();
export const DATE = new DateType();

export function ARRAY<E>(itemType: TypeInfo<E>): ListType<E> {
    return new ArrayType<E>(itemType);
}

export function LIST<E>(itemType: TypeInfo<E>): ListType<E> {
    return new ListType<E>(itemType);
}

export function SET<E>(itemType: TypeInfo<E>): SetType<E> {
    return new SetType<E>(itemType);
}

export function MAP<K, V>(keyType: TypeInfo<K>, valueType: TypeInfo<V>): MapType<K, V> {
    return new MapType<K, V>(keyType, valueType);
}

export const INET_ADDRESS = new InetAddressType();

export const typeMap = {
    'BYTE': BYTE,
    'SHORT': SHORT,
    'INT': INT,
    'LONG': LONG,
    'FLOAT': FLOAT,
    'DOUBLE': DOUBLE,
    'BIG_INTEGER': BIG_INTEGER,
    'BIG_DECIMAL': BIG_DECIMAL,
    'BOOLEAN': BOOLEAN,
    'CHAR': CHAR,
    'STRING': STRING,
    'ENUM': ENUM,
    'DATE': DATE,
    'INET_ADDRESS': INET_ADDRESS,
};
