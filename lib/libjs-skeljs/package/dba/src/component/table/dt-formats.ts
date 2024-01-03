
type NumFormatter = (n: number) => string;

type Styler = (format: string, data: any) => string;

class DefaultStyler {

    format(format: string, data: any): string {
        let _this: any = this;
        let fn = _this[format];
        console.log(fn);
        if (fn == undefined)
            throw "invalid format: " + format;
        return fn(data);
    }

    decimal(n: number, scale: number): string | NumFormatter {
        if (scale == null) {
            scale = n;
            return function (n: number) {
                return toFixed(n, scale);
            };
        } else {
            return toFixed(n, scale);
        }
    }

    decimal1(n: number): string {
        return toFixed(n, 1);
    }

    decimal2(n: number): string {
        return toFixed(n, 2);
    }

    integer(n: number): string {
        return toFixed(n, 0);
    }

    percent(n: number, scale: number): string | NumFormatter {
        if (scale == null) {
            scale = n;
            return function (n: number) {
                n *= 100;
                return toFixed(n, scale) + "%";
            };
        } else {
            n *= 100;
            return toFixed(n, scale) + "%";
        }
    }

    percent1(n: number): string {
        n *= 100;
        return toFixed(n, 1) + "%";
    }

    percent2(n: number): string {
        n *= 100;
        return toFixed(n, 2) + "%";
    }

};

function toFixed(o: any, len: number): string {
    const num = Number(o);
    return num.toFixed(len);
}
