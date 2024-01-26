export type NamingFunc = (propv: string[]) => string;

export interface ObjType {
    [property: string]: ColumnType | ObjType
}

export interface ColumnType {
    primaryKey?: boolean
    format?: (val: any) => string
}

interface DDLFragment {
    columns: string[]
    qVals: string[]
    types: ColumnType[]
}

function lastOf(propv: string[]) {
    return propv[propv.length - 1];
}

export class Converter {
    objType: ObjType
    naming: NamingFunc
    propv: string[]
    fragment: DDLFragment

    constructor(meta: ObjType = {}, naming: NamingFunc = lastOf) {
        this.objType = meta;
        this.naming = naming;
        this.reset();
    }

    reset() {
        this.propv = [];
        this.fragment = {
            columns: [],
            qVals: [],
            types: [],
        };
    }

    convert(obj: any, type: ObjType | undefined = this.objType): DDLFragment {
        for (let property in obj) {
            let val = obj[property];
            if (val == null) continue;

            this.propv.push(property);

            let _class = Object.prototype.toString.call(val);
            if (_class == '[object Object]') {
                let propType;
                if (type != null)
                    propType = type[property] as ObjType;
                this.convert(val, propType);
            } else {
                let columnName = this.naming(this.propv);
                let qVal: string;
                let propType;
                if (type != null)
                    propType = type[property];
                if (propType?.format)
                    qVal = propType.format(val);
                else
                    switch (typeof val) {
                        case 'number':
                        case 'boolean':
                            qVal = val.toString(); break;

                        case 'string':
                        default:
                            qVal = this.qStr(val.toString()); break;
                    }
                this.fragment.columns.push(columnName);
                this.fragment.qVals.push(qVal);
                this.fragment.types.push(propType);
            }
            this.propv.pop();
        }
        return this.fragment;
    }

    toInsert(table: string, ...objv: any[]) {
        let qTable = this.qName(table);
        let sql = '';
        objv.forEach(o => {
            this.reset();
            this.convert(o);
            sql += 'insert into ' + qTable
                + '(' + this.fragment.columns.join(', ') + ')' //
                + ' values(' + this.fragment.qVals.join(', ') + ');\n';
        });
        return sql;
    }

    toUpdate(table: string, meta: ObjType, ...objv: any[]) {
        let qTable = this.qName(table);
        let sql = '';
        objv.forEach(o => {
            let sets = '';
            let where = '';
            for (let i = 0; i < this.fragment.columns.length; i++) {
                let column = this.fragment.columns[i];
                let qVal = this.fragment.qVals[i];
                let type = this.fragment.types[i];
                if (type != null)
                    if (type.primaryKey) {
                        if (where.length) where += ' and ';
                        where += column + ' = ' + qVal;
                        continue;
                    }
                if (sets.length) sets += ', ';
                sets += column + ' = ' + qVal;
            }
            let update = 'update table ' + qTable
                + ' set ' + sets;
            if (where.length)
                update += ' where ' + where;
            sql += update + ";\n";
        });
        return sql;
    }

    qName(name: string) {
        return '"' + name + '"';
    }

    qStr(s: string) {
        s = s.replace("'", "''");
        return "'" + s + "'";
    }

}
