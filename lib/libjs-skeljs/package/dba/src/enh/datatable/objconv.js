
export function objv2Tab(objv) {
    var colstat = {};
    for (var i = 0; i < objv.length; i++) {
        var obj = objv[i];
        if (obj == null) continue;
        for (var k in obj) {
            colstat[k] ||= 0;
            colstat[k]++;
        }
    }
    var cv = Object.keys(colstat);
    var columns = cv.map( (c) => ({ title: c}) );
    var data = [];
    for (var i = 0; i < objv.length; i++) {
        var obj = objv[i];
        var row = [];
        for (var ci = 0; ci < cv.length; ci++) {
            var k = cv[ci];
            var val = obj[k];
            row.push(val);
        }
        data.push(row);
    }
    return {
        columns: columns, 
        data: data
    };
}

export function tab2Objv(tab) {
    var cv = [];
    for (var i = 0; i < tab.columns.length; i++)
        cv[i] = tab.columns[i].title || tab.columns[i];
    var ov = [];
    for (var i = 0; i < tab.data.length; i++) {
        var row = tab.data[i];
        var obj = {};
        for (var j = 0; j < cv.length; j++)
            obj[cv[j]] = row[j];
        ov.push(obj);
    }
    return ov;
}

export function row2Obj(row, columns) {
    var obj = {};
    for (var i = 0; i < columns.length; i++) {
        var col = columns[i];
        var title = col.title || col;
        obj[title] = row[i];
    }
    return obj;
}
