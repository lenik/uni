import type { Moment } from "moment-timezone";
import MomentWrapper from "./MomentWapper";
import MomentWrapperType from "./MomentWapperType";
import Instant from "./Instant";
import { ISO_LOCAL_DATE_TIME, UI_DATE_TIME } from "./formats";

export class LocalDateTimeType extends MomentWrapperType<LocalDateTime> {

    get name() { return "LocalDateTime"; }
    get icon() { return "far-clock"; }
    get description() { return "a local date time."; }

    override create(): LocalDateTime {
        return LocalDateTime.now();
    }

}

export class LocalDateTime extends MomentWrapper {

    static readonly TYPE = new LocalDateTimeType();
    static DEFAULT_FORMAT = ISO_LOCAL_DATE_TIME;

    constructor(inp?: moment.MomentInput, format?: string) {
        super(format || LocalDateTime.DEFAULT_FORMAT, inp);
    }

    clone(m?: Moment) {
        return new LocalDateTime(m == null ? this.momentConst : m, this.defaultFormat);
    }

    static parse(s: string, format = LocalDateTime.DEFAULT_FORMAT) {
        return new LocalDateTime(s, format);
    }

    static now() {
        return new LocalDateTime();
    }

    static of(year: number, month: number, dayOfMonth: number, hour: number, minute: number, second = 0, nanoSecond = 0) {
        let o = new LocalDateTime(0);
        o.year = year;
        o.month = month;
        o.dayOfMonth = dayOfMonth;
        o.hour = hour;
        o.minute = minute;
        o.second = second;
        o.nanosecond = nanoSecond;
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
        return new LocalDateTime(instant.moment);
    }

    get isoFormat() {
        return this.format(ISO_LOCAL_DATE_TIME);
    }
    set isoFormat(val: string) {
        this.parse(ISO_LOCAL_DATE_TIME, val);
    }

    get uiFormat() {
        return this.format(UI_DATE_TIME);
    }
    set uiFormat(val: string) {
        this.parse(UI_DATE_TIME, val);
    }

}

export default LocalDateTime;
