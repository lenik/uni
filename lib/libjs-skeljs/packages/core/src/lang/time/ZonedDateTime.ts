import Instant from "./Instant";
import { MomentWrapper, MomentWrapperType, ZoneId, defaultUtcOffset } from "./MomentWapper";

export class ZonedDateTimeType extends MomentWrapperType<ZonedDateTime>{

    get name() { return "ZonedDateTime"; }
    get icon() { return "far-clock"; }
    get description() { return "a zoned date time."; }

    override create(): ZonedDateTime {
        return ZonedDateTime.now(defaultUtcOffset);
    }

}

export class ZonedDateTime extends MomentWrapper {

    static readonly TYPE = new ZonedDateTimeType();

    static DATE_TIME = 'YYYY-MM-DD hh:mm:ss Z';
    static ISO_LOCAL_DATE_TIME = 'YYYY-MM-DDThh:mm:ssZ';
    static DEFAULT_FORMAT = this.ISO_LOCAL_DATE_TIME;

    constructor(tz?: ZoneId, inp?: moment.MomentInput, format?: string) {
        super(format || ZonedDateTime.DEFAULT_FORMAT, inp);
        if (tz != null)
            if (typeof tz == 'number')
                this.utcOffset = tz;
            else
                this.tz = tz;
    }

    static parse(s: string, tz?: ZoneId, format = ZonedDateTime.DEFAULT_FORMAT) {
        return new ZonedDateTime(tz, s, format);
    }

    static now(tz: ZoneId) {
        return new ZonedDateTime(tz);
    }

    static of(year: number, month: number, dayOfMonth: number, hour: number, minute: number, second, nanoSecond, tz: ZoneId) {
        let o = new ZonedDateTime(tz, 0);
        o.year = year;
        o.month = month;
        o.dayOfMonth = dayOfMonth;
        o.hour = hour;
        o.minute = minute;
        o.second = second;
        o.nanosecond = nanoSecond;
        return o;
    }

    static ofEpochDay(epochDay: number, tz: ZoneId) {
        let epochSecond = epochDay * 24 * 60 * 60;
        return this.ofEpochSecond(epochSecond, tz);
    }

    static ofEpochSecond(epochSecond: number, tz: ZoneId) {
        let instant = Instant.ofEpochSecond(epochSecond);
        return this.ofInstant(instant, tz);
    }

    static ofInstant(instant: Instant, tz: ZoneId) {
        return new ZonedDateTime(tz, instant.moment);
    }

    get isoFormat() {
        return this.format(ZonedDateTime.ISO_LOCAL_DATE_TIME);
    }
    set isoFormat(val: string) {
        this.parse(ZonedDateTime.ISO_LOCAL_DATE_TIME, val);
    }

    get date_time() {
        return this.format(ZonedDateTime.DATE_TIME);
    }
    set date_time(val: string) {
        this.parse(ZonedDateTime.DATE_TIME, val);
    }

}

export default ZonedDateTime;
