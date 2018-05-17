import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClientAccountDB {
    public ClientAccountDB() {
        try {
            Connection c = DriverManager.getConnection("jdbc:sqlite:ClientsAccount.db");
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Created/Found ClientsAccount database successfully");
//        // 调用建表函数
//        EventsDB_Create();
    }

    // 与数据库建立连接
    public Connection AccountDB_Connect() {
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:ClientsAccount.db");
            System.out.println("Connected ClientsAccount database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }

    // 建表
    public void AccountDB_Create() {
        Connection c = AccountDB_Connect();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql =
                    "create table if not exists ClientsAccount(" +
                            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                            "account VARCHAR(127)," +
                            "password VARCHAR(127)," +
                            "isDel INTEGER DEFAULT 0" +
                            ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    // （增）插入一个ClientAccount
    public void InsertClientAccount(String a, String p) {
        Connection c = AccountDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "insert into ClientsAccount(id,account,password,isDel) " +
                    "values (null,\'" +
                    a + "\',\'" +
                    p + "\'," +
                    "0)";
            // 执行这条无返回值的sql语句
            stmt.executeUpdate(sql);
            // 手动提交
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Account created successfully");
    }

    // （查）根据account与password，返回一个id
    public int QueryIdByAP(String a, String p) {
        int out = 0;
        Connection c = AccountDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            // isDel=0表示该事件存在
            String sql = "select * from ClientsAccount where account=\'" +
                    a + "\' and password=\'" +
                    p + "\' and isDel=0";
            ResultSet rs = stmt.executeQuery(sql);
            // 判断ResultSet中是否有数据
            if (rs.next())
                out = rs.getInt("id");
            // 手动提交
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        if (out != 0)
            System.out.println("Selected by a&p successfully Return the id");
        else
            System.out.println("Here is no such an a&p");
        return out;
    }

    // （查）根据account，返回一个password
    public String QueryIdByAccount(String a) {
        String out = "";
        Connection c = AccountDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            // isDel=0表示该事件存在
            String sql = "select * from ClientsAccount where account=\'" +
                    a + "\' and isDel=0";
            ResultSet rs = stmt.executeQuery(sql);
            // 判断ResultSet中是否有数据
            if (rs.next())
                out = rs.getString("password");
            // 手动提交
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        if (out != "")
            System.out.println("Selected by account successfully Return the password");
        else
            System.out.println("Here is no such an account");
        return out;
    }
}
