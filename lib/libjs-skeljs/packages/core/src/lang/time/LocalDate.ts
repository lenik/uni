import MomentWrapper from "./MomentWapper";
import Instant from "./Instant";

export class LocalDate extends MomentWrapper {

    static YYYY_MM_DD = 'YYYY-MM-DD';
    static YY_MM_DD = 'YY-MM-DD';
    static YYYYMMDD = 'YYYYMMDD';
    static YYMMDD = 'YYMMDD';
    static ISO_LOCAL_DATE = this.YYYY_MM_DD;
    static DEFAULT_FORMAT = this.ISO_LOCAL_DATE;

    constructor(inp?: moment.MomentInput, format?: string) {
        super(format || LocalDate.DEFAULT_FORMAT, inp);
    }

    static parse(s: string, fmt = LocalDate.DEFAULT_FORMAT) {
        return new LocalDate(s, fmt);
    }

    static now() {
        return new LocalDate();
    }

    static of(year: number, month: number, dayOfMonth = 0) {
        let o = new LocalDate(0);
        o.year = year;
        o.month = month;
        o.dayOfMonth = dayOfMonth;
        return o;
    }

    static ofEpochDay(epochDay: number) {
        let epochSecond = epochDay * 24 * 60 * 60;
        return this.ofEpochSecond(epochSecond);
    }

    static ofEpochSecond(epochSecond: number) {
        let instant = Instant.ofEpochSecond(epochSecond);
        return this.ofInstant(instant);
    }

    static ofInstant(instant: Instant) {
        return new LocalDate(instant.moment);
    }

    get isoFormat() {
        return this.format(LocalDate.ISO_LOCAL_DATE);
    }

    set isoFormat(s: string) {
        this.parse(s, LocalDate.ISO_LOCAL_DATE);
    }

    get yyyy_mm_dd() {
        return this.format(LocalDate.DEFAULT_FORMAT);
    }

    set yyyy_mm_dd(s: string) {
        this.parse(s, LocalDate.DEFAULT_FORMAT);
    }

    get yy_mm_dd() {
        return this.format(LocalDate.YY_MM_DD);
    }

    set yy_mm_dd(s: string) {
        this.parse(s, LocalDate.YY_MM_DD);
    }

    get yyyymmdd() {
        return this.format(LocalDate.YYYYMMDD);
    }

    set yyyymmdd(s: string) {
        this.parse(s, LocalDate.YYYYMMDD);
    }

    get yymmdd() {
        return this.format(LocalDate.YYMMDD);
    }

    set yymmdd(s: string) {
        this.parse(s, LocalDate.YYMMDD);
    }

}

export default LocalDate;
