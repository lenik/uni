import type { Moment } from "moment-timezone";
import moment from "moment-timezone";
import type { ZoneId } from "./MomentWapper";
import MomentWrapper, { defaultUtcOffset } from "./MomentWapper";
import MomentWrapperType from "./MomentWapperType";
import Instant from "./Instant";
import { ISO_ZONED_DATE_TIME, UI_DATE_TIME } from "./formats";

export class ZonedDateTimeType extends MomentWrapperType<ZonedDateTime> {

    get name() { return "ZonedDateTime"; }
    get icon() { return "far-clock"; }
    get description() { return "a zoned date time."; }

    override create(): ZonedDateTime {
        return ZonedDateTime.now(defaultUtcOffset);
    }

}

export class ZonedDateTime extends MomentWrapper {

    static readonly TYPE = new ZonedDateTimeType();
    static readonly DEFAULT_FORMAT = ISO_ZONED_DATE_TIME;

    constructor(tz?: ZoneId, inp?: moment.MomentInput, format?: string) {
        super(format || ZonedDateTime.DEFAULT_FORMAT, inp);
        if (tz != null)
            if (typeof tz == 'number')
                this.utcOffset = tz;
            else
                this.tz = tz;
    }

    clone(m?: Moment) {
        return new ZonedDateTime(this.tz, m == null ? this.momentConst : m, this.defaultFormat);
    }

    format(fmt?: string | undefined): string {
        const BzzB = "[\[]zz[\]]";
        let zzOpt = false;
        let realFormat = fmt || this.defaultFormat;
        if (realFormat.endsWith(BzzB)) {
            realFormat = realFormat.substring(0, realFormat.length - BzzB.length);
            zzOpt = true;
        }
        if (realFormat === ISO_ZONED_DATE_TIME) {
            zzOpt = true;
        }

        let s = super.format(realFormat);

        if (zzOpt) {
            let tz = this.tz;
            if (tz != null)
                s += "[" + tz + "]";
        }
        return s;
    }

    static parse(s: string, tz?: ZoneId, format = ZonedDateTime.DEFAULT_FORMAT) {
        const BzzB = "[\[]zz[\]]";
        let zzOpt = false;
        let realFormat = format;
        if (realFormat.endsWith(BzzB)) {
            realFormat = realFormat.substring(0, realFormat.length - BzzB.length);
            zzOpt = true;
        }

        let bracket = s.lastIndexOf('[');
        if (bracket != -1) {
            if (!s.endsWith("]"))
                throw new Error("invalid zone-id: " + s.substring(bracket));
            let zoneIdStr = s.substring(bracket + 1, s.length - 1);
            s = s.substring(0, bracket - 1);
            if (tz == null) tz = zoneIdStr;
        }
        return new ZonedDateTime(tz, s, realFormat);
    }

    static now(tz?: ZoneId) {
        return new ZonedDateTime(tz);
    }

    static of(year: number, month: number, dayOfMonth: number,
        hour: number, minute: number, second: number, nanoSecond: number,
        tz: ZoneId) //
    {
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
        return this.format(ISO_ZONED_DATE_TIME);
    }
    set isoFormat(val: string) {
        this.parse(ISO_ZONED_DATE_TIME, val);
    }

    get uiFormat() {
        return this.format(UI_DATE_TIME);
    }
    set uiFormat(val: string) {
        this.parse(UI_DATE_TIME, val);
    }

}

export default ZonedDateTime;
