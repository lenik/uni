import MomentWrapper from "./MomentWapper";
import MomentWrapperType from "./MomentWapperType";
import Instant from "./Instant";
import { ISO_LOCAL_DATE, UI_DATE, YYMMDD, YYYYMMDD, YY_MM_DD } from "./formats";

export class LocalDateType extends MomentWrapperType<LocalDate>{

    get name() { return "LocalDate"; }
    get icon() { return "far-clock"; }
    get description() { return "a local date."; }

    override create(): LocalDate {
        return LocalDate.now();
    }

}

export class LocalDate extends MomentWrapper {

    static readonly TYPE = new LocalDateType();
    static DEFAULT_FORMAT = ISO_LOCAL_DATE;

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
        return this.format(ISO_LOCAL_DATE);
    }

    set isoFormat(s: string) {
        this.parse(s, ISO_LOCAL_DATE);
    }

    get uiFormat() {
        return this.format(UI_DATE);
    }

    set uiFormat(s: string) {
        this.parse(s, UI_DATE);
    }

    get yy_mm_dd() {
        return this.format(YY_MM_DD);
    }

    set yy_mm_dd(s: string) {
        this.parse(s, YY_MM_DD);
    }

    get yyyymmdd() {
        return this.format(YYYYMMDD);
    }

    set yyyymmdd(s: string) {
        this.parse(s, YYYYMMDD);
    }

    get yymmdd() {
        return this.format(YYMMDD);
    }

    set yymmdd(s: string) {
        this.parse(s, YYMMDD);
    }

}

export default LocalDate;
