package net.bodz.timetab.schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import net.bodz.bas.db.ctx.DataContext;
import net.bodz.bas.db.ctx.DataHub;

public class DemoMapperTest
        extends Assert {

    DataContext dataContext = DataHub.getPreferredHub().getTest();

    @Test
    public void testGetConnection()
            throws SQLException {
        Connection cn = dataContext.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery("select 1");
        rs.next();
        int num = rs.getInt(1);
        assertEquals(1, num);
        rs.close();
        st.close();
        cn.close();
    }

}
