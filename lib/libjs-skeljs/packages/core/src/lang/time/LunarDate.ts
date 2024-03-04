import moment from 'moment-timezone';
import { Moment } from 'moment-timezone';
import { Solar, Lunar } from 'lunar-javascript';
import TypeInfo from '../TypeInfo';

export class LunarDateType extends TypeInfo<LunarDate>{

    get name() { return "LunarDate"; }
    get icon() { return "far-clock"; }
    get description() { return "a lunar local date."; }

    parse(s: string): LunarDate {
        throw 'not implemented';
    }

    format(val: LunarDate): string {
        throw 'not implemented';
    }

}

export class LunarDate {

    static TYPE = new LunarDateType();

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

    static now() {
        return this.fromMoment(moment());
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

export default LunarDate;
