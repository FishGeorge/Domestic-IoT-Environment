import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DateEventDB {
    private String account = "admin";

    public DateEventDB(String a) {
        account = a;
        try {
            Connection c = DriverManager.getConnection("jdbc:sqlite:DateEvents.db");
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Created/Found database successfully");
        // 调用建表函数
        EventDB_Create();
    }

    // 与数据库建立连接
    public Connection EventDB_Connect() {
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:DateEvents.db");
            System.out.println("Connected database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }

    // 建表
    public void EventDB_Create() {
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql =
                    "create table if not exists Events_" +
                            account + "(" +
                            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                            "title VARCHAR(127)," +
                            "date INTEGER," +
                            "period_start INTEGER," +
                            "period_finish INTEGER," +
                            "content VARCHAR(127)," +
                            "location VARCHAR(127)," +
                            "remark VARCHAR(127)," +
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

    // （增）插入一个DateEvent
    public void InsertDateEvent(DateEvent p) {
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "insert into Events_" +
                    account + "(id,title,date,period_start,period_finish,content,location,remark,isDel) " +
                    "values (null,\'" +
                    p.getTitle() + "\'," +
                    p.getDate() + "," +
                    p.getPeriod()[0] + "," +
                    p.getPeriod()[1] + ",\'" +
                    p.getContent() + "\',\'" +
                    p.getLocation() + "\',\'" +
                    p.getRemark() + "\'," +
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
        System.out.println("Events created successfully");
    }

    // （删）根据数据库id删除event
    public void DeleteDateEventById(int id) {
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = ("update Events_" +
                    account + " set isDel=1 where id=" + String.valueOf(id));
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
        System.out.println("Events deleted by id successfully");
    }

    // （删）删除所有event
    public void DeleteAllDateEvent() {
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "update Events_" +
                    account + " set isDel=1 where isDel=0";
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
        System.out.println("All events deleted successfully");
    }

    // （查）根据Date查阅DateEvent，返回一个List
    public List<DateEvent> QueryListByDate(int date) {
        List<DateEvent> out = new ArrayList<DateEvent>();
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            // isDel=0表示该事件存在
            String sql = "select * from Events_" +
                    account + " where date=" + String.valueOf(date) + " and isDel=0";
            ResultSet rs = stmt.executeQuery(sql);
            // 判断ResultSet中是否有数据
            while (rs.next()) {
                // 如果有DateEvent，则把查到的值填充到一个DateEvent实体
                DateEvent addEvent = new DateEvent();
                addEvent.setTitle(rs.getString("title"));
                addEvent.setDate(rs.getInt("date"));
                addEvent.setPeriod(new int[]{rs.getInt("period_start"), rs.getInt("period_finish")});
                addEvent.setContent(rs.getString("content"));
                addEvent.setLocation(rs.getString("location"));
                addEvent.setRemark(rs.getString("remark"));
                //
                out.add(addEvent);
            }
            // 手动提交
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Selected events by date successfully Return a list");
        return out;
    }

    // （查）根据DateEvent，返回一个id
    public int QueryIdByDateEvent(DateEvent p) {
        int out = 0;
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            // isDel=0表示该事件存在
            String sql = "select * from Events_" +
                    account + " where title=\'" +
                    p.getTitle() + "\' and date=" +
                    String.valueOf(p.getDate()) + " and isDel=0";
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
        System.out.println("Selected events by dateevent successfully Return an id");
        return out;
    }

    // （查）返回所有未删事件
    public List<DateEvent> getAllDateEvents() {
        List<DateEvent> out = new ArrayList<DateEvent>();
        Connection c = EventDB_Connect();
        Statement stmt = null;
        try {
            // 关闭自动提交
            c.setAutoCommit(false);
            stmt = c.createStatement();
            // isDel=0表示该事件存在
            String sql = "select * from Events_" +
                    account + " where isDel=0";
            ResultSet rs = stmt.executeQuery(sql);
            // 判断ResultSet中是否有数据
            while (rs.next()) {
                // 如果有DateEvent，则把查到的值填充到一个DateEvent实体
                DateEvent addEvent = new DateEvent();
                addEvent.setTitle(rs.getString("title"));
                addEvent.setDate(rs.getInt("date"));
                addEvent.setPeriod(new int[]{rs.getInt("period_start"), rs.getInt("period_finish")});
                addEvent.setContent(rs.getString("content"));
                addEvent.setLocation(rs.getString("location"));
                addEvent.setRemark(rs.getString("remark"));
                //
                out.add(addEvent);
            }
            // 手动提交
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Get all events successfully Return a list");
        return out;
    }
}
