import moment from 'moment-timezone';
import { Moment } from 'moment-timezone';
import { Solar, Lunar, LunarYear } from 'lunar-javascript';

export class MomentWrapper {
    moment: Moment
    version = 0
    defaultFormat: string

    constructor(defaultFormat: string, inp?: any) {
        this.defaultFormat = defaultFormat;
        if (inp instanceof moment) {
            this.moment = inp as Moment;
        } else {
            this.moment = moment(inp);
        }
    }

    format(fmt?: string) {
        return this.moment_read.format(fmt || this.defaultFormat);
    }

    parse(s: string, fmt?: string) {
        this.moment = moment(s, fmt || this.defaultFormat);
    }

    toString() {
        return this.format(this.defaultFormat);
    }

    get string() {
        return this.format();
    }
    set string(val: string) {
        this.parse(val);
    }

    get _date() {
        let tz = moment.tz.guess();
        let m = this.moment.clone().tz(tz);
        return m.toDate();
    }
    set _date(val: Date) {
        let tz = moment.tz.guess();
        this.moment = moment.tz(val, tz);
    }

    get moment_read() {
        this.version == 0;
        return this.moment;
    }
    get moment_write() {
        this.version++;
        return this.moment;
    }

    get year() { return this.moment_read.year() }
    set year(val: number) { this.moment_write.year(val) }

    get quarter() { return this.moment_read.quarter() }
    set quarter(val: number) { this.moment_write.quarter(val) }

    get quarters() { return this.moment_read.quarters() }
    set quarters(val: number) { this.moment_write.quarters(val) }

    get month() { return this.moment_read.month() }
    set month(val: number) { this.moment_write.month(val) }

    get month1() { return this.moment_read.month() + 1 }
    set month1(val: number) { this.moment_write.month(val - 1) }

    get monthName() { return this.moment_read.month + "" }
    set monthName(val: string) { this.moment_write.month(val) }

    get day() { return this.moment_read.day() }
    set day(val: number) { this.moment_write.day(val) }

    get days() { return this.moment_read.days() }
    set days(val: number) { this.moment_write.days(val) }

    get date() { return this.version * 0 + this.moment_read.date() }
    set date(val: number) { this.moment_write.date(val); }

    get hour() { return this.moment_read.hour() }
    set hour(val: number) { this.moment_write.hour(val) }

    get hours() { return this.moment_read.hours() }
    set hours(val: number) { this.moment_write.hours(val) }

    get minute() { return this.moment_read.minute() }
    set minute(val: number) { this.moment_write.minute(val) }

    get minutes() { return this.moment_read.minutes() }
    set minutes(val: number) { this.moment_write.minutes(val) }

    get second() { return this.moment_read.second() }
    set second(val: number) { this.moment_write.second(val) }

    get seconds() { return this.moment_read.seconds() }
    set seconds(val: number) { this.moment_write.seconds(val) }

    get millisecond() { return this.moment_read.millisecond() }
    set millisecond(val: number) { this.moment_write.millisecond(val) }

    get milliseconds() { return this.moment_read.milliseconds() }
    set milliseconds(val: number) { this.moment_write.milliseconds(val) }

    get weekday() { return this.moment_read.weekday() }
    set weekday(val: number) { this.moment_write.weekday(val) }

    get isoWeekday() { return this.moment_read.isoWeekday() }
    set isoWeekday(val: number) { this.moment_write.isoWeekday(val) }

    get isoWeekdayName() { return this.moment_read.isoWeekday() + '' }
    set isoWeekdayName(val: string) { this.moment_write.isoWeekday(val) }

    get weekYear() { return this.moment_read.weekYear() }
    set weekYear(val: number) { this.moment_write.weekYear(val) }

    get isoWeekYear() { return this.moment_read.isoWeekYear() }
    set isoWeekYear(val: number) { this.moment_write.isoWeekYear(val) }

    get week() { return this.moment_read.week() }
    set week(val: number) { this.moment_write.week(val) }

    get weeks() { return this.moment_read.weeks() }
    set weeks(val: number) { this.moment_write.weeks(val) }

    get isoWeek() { return this.moment_read.isoWeek() }
    set isoWeek(val: number) { this.moment_write.isoWeek(val) }

    get isoWeeks() { return this.moment_read.isoWeeks() }
    set isoWeeks(val: number) { this.moment_write.isoWeeks(val) }

    get weeksInYear() { return this.moment_read.weeksInYear() }

    get isoWeeksInYear() { return this.moment_read.isoWeeksInYear() }

    get isoWeeksInIsoWeekYear() { return this.moment_read.isoWeeksInISOWeekYear() }

    get dayOfYear() { return this.moment_read.dayOfYear() }
    set dayOfYear(val: number) { this.moment_write.dayOfYear(val) }

}

export class LocalDate extends MomentWrapper {

    constructor(inp?: any) {
        super('YYYY-MM-DD', inp);
    }

}

export class LocalDateTime extends MomentWrapper {

    constructor(inp?: any) {
        super('YYYY-MM-DD hh:mm:ss', inp);
    }

}

export class ZonedDateTime extends MomentWrapper {

    constructor(inp?: any) {
        super('YYYY-MM-DD hh:mm:ss', inp);
    }

}

export class OffsetDateTime extends MomentWrapper {

    constructor(inp?: any) {
        super('YYYY-MM-DD hh:mm:ss', inp);
    }

}

export class LunarDate {

    _lunar: Lunar
    onchange: (val: LunarDate) => void

    constructor(lunar: Lunar) {
        this._lunar = lunar;
    }

    static fromMoment(m: Moment) {
        let solar = Solar.fromYmd(m.year(), m.month() + 1, m.date());
        let lunar = solar.getLunar();
        return new LunarDate(lunar);
    }

    get lunar() {
        return this._lunar;
    }
    set lunar(val: Lunar) {
        this._lunar = val;
        if (this.onchange != null)
            this.onchange(this);
    }

    get year() { return this.lunar.getYear(); }
    set year(val: number) {
        this.lunar = Lunar.fromYmd(val, this.monthSigned, this.date);
    }

    get monthSigned() { return this.lunar.getMonth(); }
    set monthSigned(val: number) {
        this.lunar = Lunar.fromYmd(this.year, val, this.date);
    }

    get month() { return Math.abs(this.monthSigned) }
    set month(val: number) { this.monthSigned = val }

    get isLeapMonth() { return this.monthSigned < 0 }
    set isLeapMonth(val: boolean) {
        let num = Math.abs(this.monthSigned);
        this.monthSigned = val ? -num : num;
    }

    get date() { return this.lunar.getDay(); }
    set date(val: number) {
        this.lunar = Lunar.fromYmd(this.year, this.monthSigned, val);
    }

    get solar() { return this.lunar.getSolar(); }
    set solar(val) { this.lunar = val.getLunar(); }

    get moment() {
        let solar = this.solar;
        return moment({
            year: solar.getYear(),
            month: solar.getMonth() - 1,
            date: solar.getDay()
        });
    }
    set moment(m: Moment) {
        let l = LunarDate.fromMoment(m);
        this.lunar = l.lunar;
    }

}
