import Big from "big.js";
import {
    BigDecimal, BigInteger,
    InetAddress,
    byte, char, double, float, int, long, short,
} from "./basetype";
import TypeInfo from "./TypeInfo";

function parseIntEx(s: string) {
    let norm = normalizeNumber(s);
    return parseInt(norm);
}

function parseBigInt(s: string) {
    let norm = normalizeNumber(s);
    return BigInt(norm)
}

function parseDecimal(s: string): number {
    let norm = normalizeNumber(s);
    let n = parseFloat(norm);
    return n;
}

function normalizeNumber(s: string): string {
    let norm = s.replace(/,/g, '');
    return norm;
}

function rjoin(array: number[], delim: string, padSize = 3) {
    let PADS = '000000000000';
    let s = '';
    for (let i = array.length - 1; i >= 0; i--) {
        let n = String(array[i]);
        if (i != array.length - 1) {
            s += delim;
            let pad = PADS.substring(0, padSize - n.length);
            s += pad;
        }
        s += n;
    }
    return s;
}

function formatInteger(n: number) {
    if (n == 0) return '0';
    let negative = n < 0;
    if (negative) n = -n;
    let v: number[] = [];
    n = Math.floor(n);
    while (n != 0) {
        let rem = n % 1000;
        n = Math.floor(n / 1000);
        v.push(rem);
    }
    let s = rjoin(v, ',');
    if (negative) s = "-" + s;
    return s;
}

function formatDecimal(n: number, scale: int = 3) {
    let negative = n < 0;
    if (negative) n = -n;
    let s = negative ? '-' : '';

    let integer = Math.floor(n);
    s += formatInteger(integer);

    let base = 10 ** scale;
    let decimal = n - integer;
    decimal = Math.round(decimal * base) / base;
    if (decimal != 0) {
        let d = String(decimal);
        if (d.startsWith('0.'))
            d = d.substring(2);
        else if (d.includes('e-'))
            return _formatExpDecimal(d, scale);
        else
            throw new Error("unexpected: " + d);
        s += '.' + d;
    }
    return s;
}

function _formatExpDecimal(d: string, scale: int = 3) {
    let lastE = d.lastIndexOf('e');
    let exp = parseInt(d.substring(lastE + 1));
    let dot = d.indexOf('.');
    let head = d.substring(0, dot);
    let tail = d.substring(dot + 1, lastE);
    let dec = head + tail;
    dot += exp;
    if (dot <= 0) {
        let t = ('0'.repeat(-dot) + dec);
        if (scale != null)
            t = t.substring(0, scale);
        return '0.' + t;
    }

    if (dot >= dec.length)
        return dec + '0'.repeat(dot - dec.length);

    let t = dec.substring(dot);
    if (scale != null)
        t = t.substring(0, scale);
    return dec.substring(0, dot) + '.' + t;
}

function formatBigInt(n: BigInteger) {
    let negative = n < 0;
    if (negative) n = -n;
    let v: number[] = [];
    while (String(n) != '0') {
        let rem = n % BigInt(1000);
        n /= BigInt(1000);
        v.push(Number(rem));
    }
    let s = rjoin(v, ',');
    if (negative) s = "-" + s;
    return s;
}

function formatBigDecimal(n: BigDecimal) {
    let s = n.toString();
    let dot = s.indexOf('.');
    let integer = dot == -1 ? s : s.substring(0, dot);
    let decimal = dot == -1 ? null : s.substring(dot + 1);
    s = formatBigInt(BigInt(integer));
    if (decimal != null)
        s += '.' + decimal;
    return s;
}

export class UndefinedType extends TypeInfo<undefined> {

    override get icon() { return "far-question"; }

    override get name() { return "undefined"; }

    override create() {
        return undefined;
    }

    override parse(s: string): undefined {
        return undefined;
    }

    override format(val: undefined): string {
        return "undefined";
    }

}

export abstract class NumberType<T> extends TypeInfo<T> {

    override get icon() { return "far-hashtag"; }

    override create() {
        return 0 as T;
    }

}

export class ByteType extends NumberType<byte> {

    override get name() { return "byte"; }

    override parse(s: string): byte {
        let n = parseIntEx(s);
        let val = Math.floor(n) & 0xFF;
        return val;
    }

    override format(val: number): string {
        return formatInteger(val);
    }

}

export class ShortType extends NumberType<short> {

    override get name() { return "short"; }

    override parse(s: string): byte {
        let n = parseIntEx(s);
        let val = Math.floor(n) & 0xFFFF;
        return val;
    }

    override format(val: number): string {
        return formatInteger(val);
    }

}

export class IntType extends NumberType<int> {

    override get name() { return "int"; }

    override parse(s: string) {
        let n = parseIntEx(s);
        let val = Math.floor(n) & 0xFFFF_FFFF;
        return val;
    }

    override format(val: number): string {
        return formatInteger(val);
    }

}

export class LongType extends NumberType<long> {

    override get name() { return "long"; }
    override get description() { return "long int"; }

    override parse(s: string) {
        let n = parseIntEx(s);
        n = Math.floor(n) & 0xFFFF_FFFF_FFFF_FFFF;
        return n;
    }

    override format(val: number): string {
        return formatInteger(val);
    }

}

export class FloatType extends NumberType<float> {

    override get name() { return "float"; }

    override parse(s: string) {
        let n = parseDecimal(s);
        return n;
    }

    override format(val: number): string {
        return formatDecimal(val);
    }

}

export class DoubleType extends NumberType<double> {

    override get name() { return "double"; }

    override parse(s: string) {
        let n = parseDecimal(s);
        return n;
    }

    override format(val: number): string {
        return formatDecimal(val);
    }

}

export class BigIntegerType extends NumberType<BigInteger> {

    override get name() { return "BigInteger"; }

    override create() {
        return BigInt(0);
    }
    
    override parse(s: string): BigInteger {
        let b = parseBigInt(s);
        return b;
    }

    override format(val: BigInteger): string {
        return formatBigInt(val);
    }

    override fromJson(jv: any): bigint {
        let s = jv as string;
        return BigInt(s);
    }

    override toJson(val: bigint) {
        return String(val);
    }

}

export class BigDecimalType extends NumberType<BigDecimal> {

    override get name() { return "BigDecimal"; }

    override create() {
        return Big(0);
    }
    
    override parse(s: string): BigDecimal {
        let norm = normalizeNumber(s);
        let b = Big(norm);
        return b;
    }

    override format(val: BigDecimal): string {
        return formatBigDecimal(val);
    }

    override fromJson(jv: any): BigDecimal {
        let str = jv as string;
        return Big(str);
    }

    override toJson(val: BigDecimal) {
        return String(val);
    }

}

export class BooleanType extends TypeInfo<boolean> {

    override get name() { return "boolean"; }

    override create() {
        return false;
    }
    
    override parse(s: string): boolean {
        switch (s) {
            case "true":
            case "yes":
                return true;
            case "false":
            case "no":
                return false;
        }
        let num = parseIntEx(s);
        if (num)
            return true;
        return false;
    }

}

export class CharType extends TypeInfo<char> {

    override get name() { return "char"; }
    override get icon() { return "fa-font"; }

    override create() {
        return '\0';
    }
    
    override parse(s: string) {
        if (s.length)
            return s.charAt(0);
        else
            return '\0';
    }

}

export class StringType extends TypeInfo<string> {

    override get name() { return "string"; }
    override get icon() { return "fa-font"; }

    override create() {
        return '';
    }
    
    override format(val: string): string {
        return val;
    }

    override parse(s: string) {
        return s;
    }

}

export class EnumType extends TypeInfo<string> {

    override get name() { return "enum"; }
    override get icon() { return "fas-list-ol"; }

    override create() {
        return 'XXX';
    }
    
    override format(val: string): string {
        return val;
    }

    override parse(s: string) {
        return s;
    }

}

export class DateType extends TypeInfo<Date> {

    override get name() { return "Date"; }
    override get icon() { return "fa-clock"; }

    override create() {
        return new Date();
    }
    
    override format(val: Date): string {
        return val.toISOString();
    }

    override parse(s: string): Date {
        return new Date(s);
    }

    override fromJson(jv: any): Date {
        return this.parse(jv);
    }

    override toJson(val: Date) {
        return this.format(val);
    }

}

export abstract class CollectionType<E = any, C = E[]> extends TypeInfo<C> {

    itemType: TypeInfo<E>

    constructor(itemType: TypeInfo<E>) {
        super();
        this.itemType = itemType;
    }

    override get name() { return "Collection"; }
    override get icon() { return "far-cube"; }

    abstract override create(): C;
    abstract add(collection: C, item: E): boolean;
    abstract remove(collection: C, item: E): boolean;
    abstract toArray(collection: C): E[];

    override format(val: C): string {
        let array = this.toArray(val);
        return JSON.stringify(array);
    }

    override parse(s: string): C {
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

    override get name() { return "Array"; }
    override get icon() { return "far-cube"; }

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

    override fromJson(jv: any): E[] {
        let array: E[] = [];
        for (let i = 0; i < jv.length; i++) {
            let jvItem = jv[i];
            let item = this.itemType.fromJson(jvItem);
            array.push(item);
        }
        return array;
    }

    override toJson(array: E[]) {
        let ja: any = [];
        for (let i = 0; i < array.length; i++) {
            let item = array[i];
            let jaItem = this.itemType.toJson(item);
            ja.push(jaItem);
        }
        return ja;
    }

}

export class ListType<E> extends ArrayType<E> {

    constructor(itemType: TypeInfo<E>) {
        super(itemType);
    }

    override get name() { return "List"; }
    override get icon() { return "far-cube"; }

}

export class SetType<E> extends CollectionType<E, Set<E>> {

    override get name() { return "Set"; }
    override get icon() { return "far-cube"; }

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

    override fromJson(jv: any): Set<E> {
        let set = new Set<E>();
        for (let i = 0; i < jv.length; i++) {
            let jvItem = jv[i];
            let item = this.itemType.fromJson(jvItem);
            set.add(item);
        }
        return set;
    }

    override toJson(set: Set<E>) {
        let ja: any = [];
        for (let item of set) {
            let jaItem = this.itemType.toJson(item);
            ja.push(jaItem);
        }
        return ja;
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

    override get name() { return "Map"; }
    override get icon() { return "far-cube"; }

    override create() {
        let map = new Map<K, V>();
        return map;
    }

    toObject(map: Map<K, V>): any {
        let obj: any = {};
        let keys = map.keys();
        for (let k of map.keys()) {
            let v = map.get(k);
            obj[k] = v;
        }
        return obj;
    }

    override format(val: Map<K, V>): string {
        let obj = this.toObject(val);
        return JSON.stringify(obj);
    }

    override parse(s: string): Map<K, V> {
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

    override fromJson(jv: any): Map<K, V> {
        let map = new Map<K, V>();
        for (let jKey in jv) {
            let jVal = jv[jKey];
            let key = this.keyType.fromJson(jKey);
            let value = this.valueType.fromJson(jVal);
            map.set(key, value);
        }
        return map;
    }

    override toJson(map: Map<K, V>) {
        let jo: any = {};
        for (let key of map.keys()) {
            let value = map.get(key)!;
            let jKey = this.keyType.toJson(key);
            let jVal = this.valueType.toJson(value);
            jo[jKey] = jVal;
        }
        return jo;
    }

}

export class InetAddressType extends TypeInfo<InetAddress> {

    override get name() { return "InetAddress"; }
    override get icon() { return "far-globe"; }

    create(): string {
        return '0.0.0.0';
    }
    
    override format(val: InetAddress): string {
        return val;
    }

    override parse(s: string): InetAddress {
        return s;
    }

    override fromJson(jv: any): InetAddress {
        let str = jv as string;
        return this.parse(str);
    }

    override toJson(val: InetAddress) {
        return this.format(val);
    }

}

export const UNDEFINED = new UndefinedType();
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
// export const DATE = new DateType();

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
    'UNDEFINED': UNDEFINED,
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
    // 'DATE': DATE,
    'INET_ADDRESS': INET_ADDRESS,
};
