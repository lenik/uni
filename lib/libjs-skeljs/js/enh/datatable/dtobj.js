
function tab2Objv(tab) {
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

function row2Obj(row, columns) {
    var obj = {};
    for (var i = 0; i < columns.length; i++) {
        var col = columns[i];
        var title = col.title || col;
        obj[title] = row[i];
    }
    return obj;
}

