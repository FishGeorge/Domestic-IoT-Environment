import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DAO {
    // JDBC 驱动名
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // 数据库实例名
    private static final String DBname = "LabSRS";
    // 数据库 URL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DBname + "?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";
    // 数据库用户名与密码
    private static final String USER = "root";
    private static final String PASS = "123456";

    // 连接对象
    private Connection con = null;
    // 语句对象
    private PreparedStatement ps = null;
    // 线程池
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    // 加载驱动程序
    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("驱动加载失败");
        }
    }

    // 打开连接
    private void prepareConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException("连接异常:" + e.getMessage());
        }
    }

    // 关闭连接
    private void closeConnection() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException("关闭连接异常:" + e.getMessage());
        }
    }

    // 操作回滚
    private void rollback() {
        try {
            con.rollback();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException("回滚失败:" + e.getMessage());
        }
    }

    // 执行语句
    public void executeSql(String sql) {
        prepareConnection();
        try {
            //将sql语句提交到数据库进行预编译
            ps = con.prepareStatement(sql);
            int i = ps.executeUpdate();
//            if (i > 0) System.out.println("excuteSql successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // 数据库操作
    // （增）写入到CommRecord
    public void Insert_CommRecord(String date, String time, String dirc, String to, String msg) {
        cachedThreadPool.execute(() -> {
            String sql = "INSERT INTO `labsrs`.`commrecord` (`date`, `time`, `dirc`, `to`, `msg`) VALUES ('" +
                    date + "','" +
                    time + "','" +
                    dirc + "','" +
                    to + "','" +
                    msg + "')";
//            String sql="INSERT INTO `labsrs`.`commrecord` (`date`, `time`, `dirc`, `to`, `msg`) VALUES ('2018-10-31', '19:39:00', 'test', 'NULL', 'NULL')";
            new DAO().executeSql(sql);
        });
    }
}