import TypeInfo from "./typeinfo";
import { BigDecimal, BigInteger, byte, char, double, float, integer, long, short } from "./basetype";
import Big from "big.js";

export abstract class NumberType<T> extends TypeInfo<T> {

    get icon() { return "far-hashtag" }

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

    get name() { return "byte"; }

    parse(s: string): byte {
        let n = parseInt(s);
        let val = Math.floor(n) & 0xFFFF;
        return val;
    }

}

export class IntegerType extends NumberType<integer> {

    get name() { return "integer"; }

    parse(s: string) {
        let n = parseInt(s);
        let val = Math.floor(n) & 0xFFFF_FFFF;
        return val;
    }

}

export class LongType extends NumberType<long> {

    get name() { return "long"; }
    get description() { return "long integer" }

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

    get name() { return "float"; }

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

export class JSONType extends TypeInfo<any> {

    get name() { return "Object"; }
    get icon() { return "far-js"; }

    format(val: any): string {
        return JSON.stringify(val);
    }

    parse(s: string): Date {
        return JSON.parse(s);
    }

}

export default TypeInfo;

export const BYTE = new ByteType();
export const SHORT = new ShortType();
export const INT = new IntegerType();
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
export const JSON = new JSONType();
