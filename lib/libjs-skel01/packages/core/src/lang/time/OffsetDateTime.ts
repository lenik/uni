import type { Moment } from "moment-timezone";
import MomentWrapper, { ZoneOffset, defaultUtcOffset } from "./MomentWapper";
import MomentWrapperType from "./MomentWapperType";
import Instant from "./Instant";
import { ISO_OFFSET_DATE_TIME, UI_OFFSET_DATE_TIME } from "./formats";

export class OffsetDateTimeType extends MomentWrapperType<OffsetDateTime> {

    get name() { return "OffsetDateTime"; }
    get icon() { return "far-clock"; }
    get description() { return "an offset date time."; }

    override create(): OffsetDateTime {
        return OffsetDateTime.now(defaultUtcOffset);
    }

}

export class OffsetDateTime extends MomentWrapper {

    static readonly TYPE = new OffsetDateTimeType();
    static readonly DEFAULT_FORMAT = ISO_OFFSET_DATE_TIME;

    constructor(offset?: ZoneOffset, inp?: moment.MomentInput, format?: string) {
        super(format || OffsetDateTime.DEFAULT_FORMAT, inp);
        if (offset != null)
            this.utcOffset = offset;
    }

    clone(m?: Moment) {
        return new OffsetDateTime(this.utcOffset, m == null ? this.momentConst : m, this.defaultFormat);
    }

    static parse(s: string, offset?: ZoneOffset, format = OffsetDateTime.DEFAULT_FORMAT) {
        return new OffsetDateTime(offset, s, format);
    }

    static now(offset?: ZoneOffset) {
        return new OffsetDateTime(offset);
    }

    static of(year: number, month: number, dayOfMonth: number, hour: number, minute: number, second, nanoSecond, offset: ZoneOffset) {
        let o = new OffsetDateTime(offset, 0);
        o.year = year;
        o.month = month;
        o.dayOfMonth = dayOfMonth;
        o.hour = hour;
        o.minute = minute;
        o.second = second;
        o.nanosecond = nanoSecond;
        return o;
    }

    static ofEpochDay(epochDay: number, offset: ZoneOffset) {
        let epochSecond = epochDay * 24 * 60 * 60;
        return this.ofEpochSecond(epochSecond, offset);
    }

    static ofEpochSecond(epochSecond: number, offset: ZoneOffset) {
        let instant = Instant.ofEpochSecond(epochSecond);
        return this.ofInstant(instant, offset);
    }

    static ofInstant(instant: Instant, offset: ZoneOffset) {
        return new OffsetDateTime(offset, instant.moment);
    }

    get isoFormat() {
        return this.format(ISO_OFFSET_DATE_TIME);
    }
    set isoFormat(val: string) {
        this.parse(ISO_OFFSET_DATE_TIME, val);
    }

    get uiFormat() {
        return this.format(UI_OFFSET_DATE_TIME);
    }
    set uiFormat(val: string) {
        this.parse(UI_OFFSET_DATE_TIME, val);
    }

}

export default OffsetDateTime;
