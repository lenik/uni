import MomentWrapper, { ZoneOffset } from "./MomentWapper";
import Instant from "./Instant";

export class OffsetTime extends MomentWrapper {

    static HH_MM_SS_Z = 'hh:mm:ssZ';
    static HHMMSS_Z = 'hhmmssZ';
    static ISO_OFFSET_TIME = this.HH_MM_SS_Z;
    static DEFAULT_FORMAT = this.ISO_OFFSET_TIME;

    constructor(offset: ZoneOffset, inp?: moment.MomentInput, format?: string) {
        super(format || OffsetTime.DEFAULT_FORMAT, inp);
        this.utcOffset = offset;
    }

    static parse(s: string, offset: ZoneOffset, format = OffsetTime.DEFAULT_FORMAT) {
        return new OffsetTime(offset, s, format);
    }

    static now(offset: ZoneOffset) {
        return new OffsetTime(offset);
    }
    F
    static of(hour: number, minute: number, second: number, nanoSecond: number, offset: ZoneOffset) {
        let o = new OffsetTime(offset, 0);
        o.hour = hour;
        o.minute = minute;
        o.second = second;
        o.nanosecond = nanoSecond;
        return o;
    }

    static ofEpochMilli(epochMilli: number, offset: ZoneOffset) {
        let instant = Instant.ofEpochMilli(epochMilli);
        return this.ofInstant(instant, offset);
    }

    static ofEpochSecond(epochSecond: number, offset: ZoneOffset) {
        let instant = Instant.ofEpochSecond(epochSecond);
        return this.ofInstant(instant, offset);
    }

    static ofSecondOfDay(n: number, offset: ZoneOffset) {
        let instant = Instant.ofEpochSecond(n);
        return this.ofInstant(instant, offset);
    }

    static ofInstant(instant: Instant, offset: ZoneOffset) {
        return new OffsetTime(offset, instant.moment);
    }

    get isoFormat() {
        return this.format(OffsetTime.ISO_OFFSET_TIME);
    }

    set isoFormat(val: string) {
        this.parse(OffsetTime.ISO_OFFSET_TIME, val);
    }

    get hh_mm_ss_z() {
        return this.format(OffsetTime.HH_MM_SS_Z);
    }

    set hh_mm_ss_z(s: string) {
        this.parse(s, OffsetTime.HH_MM_SS_Z);
    }

    get hhmmss_z() {
        return this.format(OffsetTime.HHMMSS_Z);
    }

    set hhmmss_z(s: string) {
        this.parse(s, OffsetTime.HHMMSS_Z);
    }

}

export default OffsetTime;
