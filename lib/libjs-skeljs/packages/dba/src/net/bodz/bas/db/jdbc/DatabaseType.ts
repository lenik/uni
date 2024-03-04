import Predef from '@skeljs/core/src/lang/Predef';
import PredefType from '@skeljs/core/src/lang/PredefType';

class SqlDialects {

    static MIMER_SQL = "MimerSQL";
    static MYSQL = "MySQL";
    static ORACLE = "Oracle";
    static POSTGRESQL = "PostgreSQL";
    static SQL_2023 = "MimerSQL";
    static SQL_SERVER = "SQLServer";
    static TERADATA = "Teradata";

};

export class DatabaseType extends Predef<String> {

    static _typeInfo;
    static get TYPE() {
        if (this._typeInfo == null)
            this._typeInfo = new DatabaseTypeTypeInfo();
        return this._typeInfo;
    }

    hibernateDialect: string
    driverClass: string
    urlFormat: string
    sqlDialect: string

    constructor(name: string, hibernateDialect: string, driverClass: string, urlFormat: string, sqlDialect: string) {
        super(name, name);
        this.hibernateDialect = hibernateDialect;
        this.driverClass = driverClass;
        this.urlFormat = urlFormat;
        this.sqlDialect = sqlDialect;
    }

    getConnectionUrl(properties: any): string {
        let s = this.urlFormat;
        for (let k in properties) {
            let v = properties[k];
            s = s.replace(/\$\{k\}/g, v);
        }
        return s;
    }

    /**
     * H2 Embedded Database
     */
    static H2 = new DatabaseType("H2", //
        "org.hibernate.dialect.H2Dialect", //
        "org.h2.Driver", //
        "jdbc:h2://${rootDir}/${database};DB_CLOSE_ON_EXIT=FALSE", //
        SqlDialects.SQL_2023);

    /**
     * HSQL Embedded Database
     */
    static HSQL = new DatabaseType("HSQL", //
        "org.hibernate.dialect.HSQLDialect", //
        "org.hsql.Driver", //
        "jdbc:hsql://${rootDir}/${database}", //
        SqlDialects.SQL_2023);

    /**
     * PostgreSQL RDBMS
     */
    static POSTGRESQL = new DatabaseType("POSTGRESQL", //
        "org.hibernate.dialect.PostgreSQLDialect", //
        "org.postgresql.Driver", //
        "jdbc:postgresql://${server}/${database}", //
        SqlDialects.POSTGRESQL);

    /**
     * Oracle Enterprise Database
     */
    static ORACLE = new DatabaseType("ORACLE", //
        "org.hibernate.dialect.OracleDialect", //
        "oracle.jdbc.driver.OracleDriver", //
        "jdbc:oracle://${server}/${database}", //
        SqlDialects.ORACLE);

    /**
     * Oracle Enterprise Database
     */
    static ORACLE_THIN = new DatabaseType("ORACLE_THIN", //
        "org.hibernate.dialect.OracleDialect", //
        "oracle.jdbc.driver.OracleDriver", //
        "jdbc:oracle:thin:@${server}:${database}", //
        SqlDialects.ORACLE);

    /**
     * MySQL RDBMS
     */
    static MYSQL = new DatabaseType("MYSQL", //
        "org.hibernate.dialect.MySQLDialect", //
        "org.mysql.Driver", //
        "jdbc:mysql://${server}/${database}", //
        SqlDialects.MYSQL);

}

export class DatabaseTypeTypeInfo extends PredefType<DatabaseType, String> {

    constructor() {
        super(DatabaseType);
    }

}

export default DatabaseType;
