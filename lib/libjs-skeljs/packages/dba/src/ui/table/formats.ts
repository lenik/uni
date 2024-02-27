
export type FormatFunc = (data: any) => string;

export type FormatsFunc = (format: string, data?: any) => FormatFunc | string;

export class DefaultFormats {

    format = (fmt: string, data?: any): FormatFunc | string => {
        let code = eval('this.' + fmt);
        if (code == null)
            throw "invalid format: " + fmt;
        if (data === undefined)
            return code;
        else
            return code(data);
    }

    label = (o: any) => o.label || '(n/a)';

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

    percent = (n: number, scale?: number): string | FormatFunc => {
        if (scale == null) {
            let _arg1 = n;
            return function (n: number) {
                if (Number.isNaN(n)) return 'NaN';
                n *= 100;
                return toFixed(n, _arg1) + "%";
            };
        } else {
            if (Number.isNaN(n)) return 'NaN';
            n *= 100;
            return toFixed(n, scale) + "%";
        }
    };

    percent1 = (n: number) => this.percent(n, 1);

    percent2 = (n: number) => this.percent(n, 2);

};

function toFixed(o: any, len: number): string {
    const num = Number(o);
    return num.toFixed(len);
}

export var formats = new DefaultFormats();

export default (format: string, data?: any) => {
    return formats.format(format, data);
};
