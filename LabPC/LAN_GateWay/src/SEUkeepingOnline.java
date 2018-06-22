import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SEUkeepingOnline {
    private String Username = "213161568";
    private String Password = "T3JhbmdlMDkxMA%3D%3D";

    private String BingURL = "https://cn.bing.com/";
    private String SeuURL = "http://w.seu.edu.cn/";
    private String SeuLoginURL = "http://w.seu.edu.cn/index.php/index/login";
    private String cookie;

    private HttpURLConnection conn = null;
    private OutputStream os = null;
    private InputStream is = null;

    public boolean URL_Connective(String URL) {
        int timeOut = 1000;
        boolean status=false;
        // 尝试最多3次以确定是否连接
        for(int i=3;i>0;i--) {
            try {
                URL url = new URL(URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(timeOut);
                con.connect();

                int code = con.getResponseCode();
                if (code == 200) {
                    status = true;
                    break;
                }
            } catch (Exception e) {
                status = false;
            }
        }
        return status;
    }

    public void GetCookie() {
        System.out.println("Get cookie...");
        try {
            URL url = new URL(SeuURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            cookie = conn.getHeaderField("Set-Cookie");
            System.out.println("...Done!");
            System.out.println("Set-Cookie: " + cookie);
            cookie = cookie + "; think_language=zh-Hans-CN" + "; sunriseUsername=" + Username;
            System.out.println("Modified cookie: " + cookie);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int Login() {
        System.out.println("Try to login...");
        // 检测w.seu.edu.cn连接情况
        if (!URL_Connective(SeuURL)) {
            System.out.println("Connecting w.seu.edu.cn Failed !");
            return 0;
        } else System.out.println("Connecting w.seu.edu.cn Successfully");
        // 获取cookie
        GetCookie();
        try {
            URL url = new URL(SeuLoginURL);
            conn = (HttpURLConnection) url.openConnection();
            // 命令类型 POST
            conn.setRequestMethod("POST");
            // 添加POST Header
            conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Length", "64");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("DNT", "1");
            conn.setRequestProperty("Host", "w.seu.edu.cn");
            conn.setRequestProperty("Origin", "http://w.seu.edu.cn/");
            conn.setRequestProperty("Referer", "http://w.seu.edu.cn/");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko Core/1.63.5478.400 QQBrowser/10.1.1550.400");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            // 添加cookie
            conn.setRequestProperty("cookie", cookie);
            // 使能IO
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // 发送登录命令
            String Request = "username=" + Username +
                    "&password=" + Password +
                    "&enablemacauth=0";
            os = conn.getOutputStream();
            os.write(Request.getBytes());
            // 登录成功判断
            is = conn.getInputStream();
            String response = "";
            byte[] b = new byte[1024];
            int len = is.read(b);
            while (len != -1) {
                response += new String(b, 0, len, "utf-8");
                len = is.read(b);
            }
//            System.out.println(response);
            if(response.contains("\\u8ba4\\u8bc1\\u6210\\u529f")) return 0;
            if(response.contains("\\u8ba4\\u8bc1\\u5931\\u8d25\\uff0c\\u8d26\\u6237\\u6d41\\u91cf\\u8d85\\u914d\\u989d\\u9501\\u5b9a")) return -1;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return -1;
    }

    public void Run(int timeInterval) {
        while (true) {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!URL_Connective(BingURL)) {
                System.out.println(df.format(day) + " Internet Connecting Failed !");
                if(Login()==0) System.out.println("...Login Successfully "+df.format(day));
                if(Login()==-1){
                    System.out.println("...Login Failed ! Incorrect Username or Password "+df.format(day));
                    break;
                }
            } else System.out.println(df.format(day) + " still keep online");
            // 每隔timeInterval毫秒检查一次网络状况
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SEUkeepingOnline instance = new SEUkeepingOnline();

        instance.Run(60000);
    }
}
