
export type FormatFunc = (data: any) => string;

export type FormatsFunc = (format: string, data?: any) => FormatFunc | string;

export class DefaultFormats {

    formats = (format: string, data?: any): FormatFunc | string => {
        let fn = eval('this.' + format);
        if (fn == undefined)
            throw "invalid format: " + format;
        if (data === undefined)
            return fn;
        else
            return fn(data);
    }

    decimal = (n: number, scale: number): string | FormatFunc => {
        if (scale == null) {
            scale = n;
            return function (n: number) {
                return toFixed(n, scale);
            };
        } else {
            return toFixed(n, scale);
        }
    };

    decimal1 = (n: number) => toFixed(n, 1);

    decimal2 = (n: number) => toFixed(n, 2);

    integer = (n: number) => toFixed(n, 0);

    percent = (n: number, scale: number): string | FormatFunc => {
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
    };

    percent1 = (n: number) => toFixed(n * 100, 1) + "%";

    percent2 = (n: number) => toFixed(n * 100, 2) + "%";

};

function toFixed(o: any, len: number): string {
    const num = Number(o);
    return num.toFixed(len);
}

var defaultFormats = new DefaultFormats();

export default (format: string, data?: any) => {
    return defaultFormats.formats(format, data);
};
