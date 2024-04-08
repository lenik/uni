export type CssUnit = AbsoluteCssUnit | RelativeCssUnit;
export type AbsoluteCssUnit = 'pt' | 'px' | 'mm' | 'cm' | 'in';
export type RelativeCssUnit = 'em' | 'rem';

export interface CssUnitContext {
    ppi?: number
    element?: HTMLElement
}

export function convertPoints(points: number, toUnit: CssUnit, context?: CssUnitContext) {
    switch (toUnit) {
        case 'pt':
            return points;
        case 'in':
            return points / 72;
        case 'px':
            return points * 96 / 72;
        case 'mm':
            return points * 25.4 / 72;
        case 'cm':
            return points * 2.54 / 72;

        case 'em':
        case 'rem':
            let fontSizePixels = getFontSize(context);
            return points * 96 / 72 / fontSizePixels;

        // case 'vw':
        // case 'vh':
    }
    throw new Error('unsupported unit: ' + this.unit);
}

function getContextElement(context?: CssUnitContext) {
    return context?.element || document.documentElement;
}

function getFontSize(context?: CssUnitContext) {
    let el = getContextElement(context);
    let fontSize = getComputedStyle(el).fontSize;
    return parseFloat(fontSize);
}

function getViewWidth(context?: CssUnitContext) {
    let el = getContextElement(context);
    let width = getComputedStyle(el).width;
    return CssUnitValue.parse(width);
}

function getViewHeight(context?: CssUnitContext) {
    let el = getContextElement(context);
    let width = getComputedStyle(el).height;
    return CssUnitValue.parse(width);
}

export class CssUnitValue {

    readonly value: number
    readonly unit: CssUnit = 'pt'

    constructor(value: number, unit: CssUnit) {
        this.value = value;
        this.unit = unit;
    }

    static of(value: number, unit: CssUnit) {
        return new CssUnitValue(value, unit);
    }

    static parseOpt(s: string | number | null | undefined): CssUnitValue | null | undefined {
        switch (s) {
            case null:
                return null;
            case undefined:
                return undefined;
        }
        return this.parse(s!);
    }

    static parse(s: string | number): CssUnitValue {
        if (typeof s == 'number')
            return new CssUnitValue(s as number, 'px');

        let m = s.match(/^([0-9.]+)(\w+|%)$/);
        if (m) {
            let [_, valueStr, unit] = m;
            let value = parseFloat(valueStr);
            return new CssUnitValue(value, unit as CssUnit);
        }
        throw new Error("parse error: " + s);
    }

    get devicePixels() {
        let devicePPI = window.devicePixelRatio * 96;
        return this.points / 72 * devicePPI;
    }

    get points() {
        return this.getPoints();
    }

    getPoints(context?: CssUnitContext) {
        switch (this.unit) {
            case 'pt':
                return this.value;
            case 'in':
                return this.value * 72;
            case 'px':
                return this.value * 72 / 96;
            case 'mm':
                return this.value * 72 / 25.4;
            case 'cm':
                return this.value * 72 / 2.54;

            case 'em':
                let fontSizePixels = getFontSize(context);
                return this.value * fontSizePixels * 72 / 96;

            case 'rem':
                let rootFontSizePixels = getFontSize();
                return this.value * rootFontSizePixels * 72 / 96;

            // case 'vw':
            // case 'vh':
        }
        throw new Error('unsupported unit: ' + this.unit);
    }

    to(unit: CssUnit, context?: CssUnitContext) {
        if (unit == this.unit)
            return this;
        let points = this.getPoints(context);
        let value = convertPoints(points, unit, context);
        return new CssUnitValue(value, unit);
    }

    add(o: CssUnitValue, context?: CssUnitContext) {
        let u = o.to(this.unit, context);
        return new CssUnitValue(this.value + u.value, this.unit);
    }

    sub(o: CssUnitValue, context?: CssUnitContext) {
        let u = o.to(this.unit, context);
        return new CssUnitValue(this.value - u.value, this.unit);
    }

    mul(n: number) {
        return new CssUnitValue(this.value * n, this.unit);
    }

    div(n: number) {
        return new CssUnitValue(this.value / n, this.unit);
    }

    equals(o: CssUnitValue) {
        return this.value == o.value && this.unit == o.unit;
    }

    compare(o: CssUnitValue, context?: CssUnitContext) {
        if (this.unit == o.unit)
            return Math.sign(this.value - o.value);
        let p1 = this.getPoints(context);
        let p2 = o.getPoints(context);
        return Math.sign(p1 - p2);
    }

    min(o: CssUnitValue, context?: CssUnitContext) {
        return this.compare(o, context) <= 0 ? this : o;
    }

    max(o: CssUnitValue, context?: CssUnitContext) {
        return this.compare(o, context) >= 0 ? this : o;
    }

    toString() {
        return this.value + this.unit;
    }

}

export default CssUnitValue;