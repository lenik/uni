(function() {

    Array.prototype.binarySearch = function(el) {
        var start = 0, end = this.length;
        var pos, val;
        while (start < end) {
            pos = (start + end) / 2 | 0;
            val = this[pos];
            if (val < el)
                start = pos + 1;
            else if (val > el)
                end = pos;
            else
                return pos;
        }
        return -start - 1;
    };

    Array.prototype.insertPoint = function(el) {
        var i = this.binarySearch(el);
        if (i < 0)
            i = -i - 1;
        return i;
    };

    Array.prototype.insertAfterPoint = function(el) {
        var i = this.binarySearch(el);
        if (i < 0)
            i = -i - 1;
        else
            i++;
        return i;
    };

    var lbase = moment("1900-01-31");
    var ldata = [
            0x04BD8,0x04AE0,0x0A570,0x054D5,0x0D260,0x0D950,0x16554,0x056A0,0x09AD0,0x055D2,
            0x04AE0,0x0A5B6,0x0A4D0,0x0D250,0x1D255,0x0B540,0x0D6A0,0x0ADA2,0x095B0,0x14977,
            0x04970,0x0A4B0,0x0B4B5,0x06A50,0x06D40,0x1AB54,0x02B60,0x09570,0x052F2,0x04970,
            0x06566,0x0D4A0,0x0EA50,0x06E95,0x05AD0,0x02B60,0x186E3,0x092E0,0x1C8D7,0x0C950,
            0x0D4A0,0x1D8A6,0x0B550,0x056A0,0x1A5B4,0x025D0,0x092D0,0x0D2B2,0x0A950,0x0B557,
            0x06CA0,0x0B550,0x15355,0x04DA0,0x0A5B0,0x14573,0x052B0,0x0A9A8,0x0E950,0x06AA0,
            0x0AEA6,0x0AB50,0x04B60,0x0AAE4,0x0A570,0x05260,0x0F263,0x0D950,0x05B57,0x056A0,
            0x096D0,0x04DD5,0x04AD0,0x0A4D0,0x0D4D4,0x0D250,0x0D558,0x0B540,0x0B6A0,0x195A6,
            0x095B0,0x049B0,0x0A974,0x0A4B0,0x0B27A,0x06A50,0x06D40,0x0AF46,0x0AB60,0x09570,
            0x04AF5,0x04970,0x064B0,0x074A3,0x0EA50,0x06B58,0x055C0,0x0AB60,0x096D5,0x092E0,
            0x0C960,0x0D954,0x0D4A0,0x0DA50,0x07552,0x056A0,0x0ABB7,0x025D0,0x092D0,0x0CAB5,
            0x0A950,0x0B4A0,0x0BAA4,0x0AD50,0x055D9,0x04BA0,0x0A5B0,0x15176,0x052B0,0x0A930,
            0x07954,0x06AA0,0x0AD50,0x05B52,0x04B60,0x0A6E6,0x0A4E0,0x0D260,0x0EA65,0x0D530,
            0x05AA0,0x076A3,0x096D0,0x04BD7,0x04AD0,0x0A4D0,0x1D0B6,0x0D250,0x0D520,0x0DD45,
            0x0B5A0,0x056D0,0x055B2,0x049B0,0x0A577,0x0A4B0,0x0AA50,0x1B255,0x06D20,0x0ADA0,
            0x14B63 ];

    var ymonths = [];
    var ymonthsint = [];
    var ydays = [];
    var ydaysint = [];

    function leapMonth(y) {
        return ldata[y] & 0xF;
    }

    function leapDays(y) {
        var m = leapMonth(y);
        if (m)
            return (ldata[y] & 0x10000) ? 30 : 29;
        else
            return 0;
    };

    function months(y) {
        return (ldata[y] & 0xF) ? 13 : 12;
    }

    function days(y, m) {
        if (m != null) {
            var mask = 1 << (m - 1 + 4);
            return (ldata[y] & mask) ? 30 : 29;
        } else {
            var n = 29 * 12 + leapDays(y);
            var mask = 0x8000;
            var info = ldata[y] & 0x0FFFF;
            for (var m = 0; m < 12; m++) {
                if (info & mask) n++;
                mask >>= 1;
            }
            return n;
        }
    };

    var monthSum = 0;
    var daySum = 0;
    for (var y = 0; y < 150; y++) {
        var n = months(y);
        monthSum += n;
        ymonths[y] = n;
        ymonthsint[y] = monthSum;
        n = days(y);
        daySum += n;
        ydays[y] = n;
        ydaysint[y] = daySum;
    }
    ydays[-1] = ydaysint[-1] = 0;

    var abc = "甲乙丙丁戊己庚辛壬癸";
    var anim1 = "子丑寅卯辰巳午未申酉戌亥";
    var anim2 = "鼠牛虎兔龙蛇马羊猴鸡狗猪";
    var monthNames = "正,二,三,四,五,六,七,八,九,十,十一,腊".split(",");
    var dayNames = ("初一,初二,初三,初四,初五,初六,初七,初八,初九,初十"
        + ",十一,十二,十三,十四,十五,十六,十七,十八,十九,二十"
        + ",廿一,廿二,廿三,廿四,廿五,廿六,廿七,廿八,廿九,三十").split(",");

    function LunarDate(solarDate) {
        if (solarDate == null)
            solarDate = moment();
        else if (solarDate instanceof moment)
            ;
        else
            solarDate = moment(solarDate);

        this._absDay = solarDate.diff(lbase, "days");
        this._year = ydaysint.insertAfterPoint(this._absDay);
        var lastYearAbsMonth = ymonthsint[this._year - 1] | 0;
        var lastYearAbsDay = ydaysint[this._year - 1] | 0;
        this._dayOfYear = this._absDay - lastYearAbsDay;

        var info = ldata[this._year] & 0x0FFFF;
        this.leapMonth = leapMonth(this._year);
        var mask = 0x8000;
        var mdays = [];
        var mdaysint = [];
        var sum = 0;
        for (var i = 1; i <= 12; i++) {
            var n = info & mask ? 30 : 29;
            if (i == this.leapMonth) {
                mdays.push(leapDays(this._year));
                continue;
            }
            mdays.push(n);
            mask >>= 1;
        }
        for (var i = 0; i < mdays.length; i++) {
            sum += mdays[i];
            mdaysint.push(sum);
        }

        mdaysint[-1] = 0;

        this._month = mdaysint.insertAfterPoint(this._dayOfYear);
        this._absMonth = lastYearAbsMonth + this._month;

        var lastMonthInt = mdaysint[this._month - 1];
        this._dayOfMonth = this._dayOfYear - lastMonthInt;

        this.__defineGetter__("year", function() {
            return this._year + 1900;
        });

        this.__defineGetter__("month", function() {
            var m = this._month + 1;
            return m > this.leapMonth ? m - 1 : m;
        });

        this.__defineGetter__("day", function() {
            return this._dayOfMonth + 1;
        });

        this.__defineGetter__("monthName", function() {
            var m = this._month + 1;
            if (m < this.leapMonth)
                return monthNames[m - 1];
            else if (m == this.leapMonth)
                // return "闰月";
                return "闰" + monthNames[m - 1];
            else
                return monthNames[m - 2];
        });

        this.__defineGetter__("dayName", function() {
            var n= dayNames[this._dayOfMonth];
            return n;
        });

        this.__defineGetter__("月", function() {
            var m = this._month + 1;
            return m > this.leapMonth ? m - 1 : m;
        });

        this.__defineGetter__("aYear", function() { // 庚子年
            var a = abc.charAt((this._year + 6) % 10);
            var b = anim1.charAt((this._year + 0) % 12);
            return a + b;
        });

        this.__defineGetter__("aMonth", function() { // 丁丑月 **TODO
            var m = (this._absDay / 30) | 0;
            var a = abc.charAt((m + 3) % 10);
            var b = anim1.charAt((m + 1) % 12);
            return a + b;
        });

        this.__defineGetter__("aDay", function() { // 甲辰日
            var a = abc.charAt((this._absDay + 0) % 10);
            var b = anim1.charAt((this._absDay + 4) % 12);
            return a + b;
        });

        this.__defineGetter__("dateName", function() {
            return this.aYear + "年" + this.monthName + "月" + this.dayName + "日";
        });

        this.__defineGetter__("aDate", function() {
            return this.aYear + "年" + this.aMonth + "月" + this.aDay + "日";
        });


    }

    Date.prototype.toLunar = function() {
        return new LunarDate(this);
    };

    moment.prototype.toLunar = function() {
        return new LunarDate(this);
    };

})();
