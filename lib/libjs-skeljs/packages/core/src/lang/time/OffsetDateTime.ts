import Instant from "./Instant";
import MomentWrapper, { ZoneOffset } from "./MomentWapper";

export class OffsetDateTime extends MomentWrapper {

    static DATE_TIME = 'YYYY-MM-DD hh:mm:ss Z';
    static ISO_LOCAL_DATE_TIME = 'YYYY-MM-DDThh:mm:ssZ';
    static DEFAULT_FORMAT = this.ISO_LOCAL_DATE_TIME;

    constructor(offset: ZoneOffset, inp?: moment.MomentInput, format?: string) {
        super(format || OffsetDateTime.DEFAULT_FORMAT, inp);
        this.utcOffset = offset;
    }

    static parse(s: string, offset: ZoneOffset, format = OffsetDateTime.DEFAULT_FORMAT) {
        return new OffsetDateTime(offset, s, format);
    }

    static now(offset: ZoneOffset) {
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
        return this.format(OffsetDateTime.ISO_LOCAL_DATE_TIME);
    }
    set isoFormat(val: string) {
        this.parse(OffsetDateTime.ISO_LOCAL_DATE_TIME, val);
    }

    get date_time() {
        return this.format(OffsetDateTime.DATE_TIME);
    }
    set date_time(val: string) {
        this.parse(OffsetDateTime.DATE_TIME, val);
    }

}

export default OffsetDateTime;
