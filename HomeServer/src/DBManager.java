import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBManager {
    // 线程池
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

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