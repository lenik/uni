
export default {
    decimal: function (n, scale) {
        if (scale == null) {
            scale = n;
            return function (n) {
                return toFixed(n, scale);
            };
        } else {
            return toFixed(n, scale);
        }
    },
    decimal1: function (n) {
        return toFixed(n, 1);
    },
    
    decimal2: function (n) {
        return toFixed(n, 2);
    },
    
    integer: function (n) {
        return toFixed(n, 0);
    },
    
    percent: function (n, scale) {
        if (scale == null) {
            scale = n;
            return function (n) {
                n *= 100;
                return toFixed(n, scale) + "%";
            };
        } else {
            n *= 100;
            return toFixed(n, scale) + "%";
        }
    },
    percent1: function (n) {
        n *= 100;
        return toFixed(n, 1) + "%";
    },
    percent2: function (n) {
        n *= 100;
        return toFixed(n, 2) + "%";
    },

};

function toFixed(o, n) {
    if (o == null) return null;
    const num = o * 1;
    return num.toFixed(n);
}
