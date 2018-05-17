import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        System.out.println("exceptionCaught: " + cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println("receive : " + message);
        String msg = (String) message;
        if (msg.endsWith("EOF")) {
            if (msg.startsWith("REGISTER:")) {
                String a = msg.substring((msg.indexOf("account=") + 8), msg.indexOf(",password="));
                String p = msg.substring((msg.indexOf(",password=") + 10), msg.indexOf(";EOF"));
                System.out.println("register:" + a + "," + p + " success");
                Server.clientAccountDB.InsertClientAccount(a, p);
                session.write("REGISTER:200");
            } else if (msg.startsWith("LOGIN:")) {
                String a = msg.substring((msg.indexOf("account=") + 8), msg.indexOf(",password="));
                String p = msg.substring((msg.indexOf(",password=") + 10), msg.indexOf(";EOF"));
                String pout = Server.clientAccountDB.QueryIdByAccount(a);
                int id = Server.clientAccountDB.QueryIdByAP(a, p);
                if (pout.equals("")) session.write("LOGIN:404");
                else if (id == 0) session.write("LOGIN:401");
                else if (p.equals(pout)) session.write("LOGIN:200");
            } else if (msg.startsWith("UPDATE:")) {
                String a = msg.substring((msg.indexOf("account=") + 8), msg.indexOf(";DE"));
                String event_update = msg.substring(msg.indexOf(";DE") + 1);
//                System.out.println(event_update + "   " + msg.indexOf("account="));
                // 初始化数据库接口
                Server.dateEventDB = new DateEventDB(a);
                // 清空表格
                Server.dateEventDB.DeleteAllDateEvent();
                DateEvent e = new DateEvent();
                int count = 0;
                for (; ; count++) {
                    if (event_update.startsWith("DEE")) break;
                    // 填充DateEvent
                    e.setTitle(event_update.substring((event_update.indexOf("title=") + 6), event_update.indexOf(",date=")));
                    e.setDate(Integer.parseInt(event_update.substring((event_update.indexOf(",date=") + 6), event_update.indexOf(",period_start="))));
                    e.setPeriod(new int[]{Integer.parseInt(event_update.substring((event_update.indexOf(",period_start=") + 14), event_update.indexOf(",period_finish=")))
                            , Integer.parseInt(event_update.substring((event_update.indexOf(",period_finish=") + 15), event_update.indexOf(",content=")))});
                    if (event_update.substring((event_update.indexOf(",content=") + 9), event_update.indexOf(",location=")).equals("null"))
                        e.setContent(event_update.substring((event_update.indexOf(",content=") + 9), event_update.indexOf(",location=")));
                    if (event_update.substring((event_update.indexOf(",location=") + 10), event_update.indexOf(",remark=")).equals("null"))
                        e.setContent(event_update.substring((event_update.indexOf(",location=") + 10), event_update.indexOf(",remark=")));
                    if (event_update.substring((event_update.indexOf(",remark=") + 8), event_update.indexOf(";DE")).equals("null"))
                        e.setContent(event_update.substring((event_update.indexOf(",remark=") + 8), event_update.indexOf(";DE")));
                    // 插入！
                    Server.dateEventDB.InsertDateEvent(e);
                    // 截取下一个event_update
                    event_update = event_update.substring(event_update.indexOf("DE", 3));
                }
                // 响应客户端
                session.write("UPDATE=" + String.valueOf(count) + ":200");
            } else if (msg.startsWith("DOWNLOAD:")) {
                String a = msg.substring((msg.indexOf("account=") + 8), msg.indexOf(";DE"));
                String msg_sendhead = "DOWNLOAD:account=" + a + ";";
                String msg_sendtail = "DEE;EOF";
                String events_download = "";
                // 初始化数据库接口
                Server.dateEventDB = new DateEventDB(a);
                List<DateEvent> out = Server.dateEventDB.getAllDateEvents();
                for (DateEvent e : out) {
                    events_download = events_download + "DE:title=" + e.getTitle() +
                            ",date=" + e.getDate() +
                            ",period_start=" + e.getPeriod()[0] +
                            ",period_finish=" + e.getPeriod()[1] +
                            ",content=" + e.getContent() +
                            ",location=" + e.getLocation() +
                            ",remark=" + e.getRemark() + ";";
                }
                System.out.println(events_download);
                // 响应客户端
                session.write(msg_sendhead + events_download + msg_sendtail);
            }
        }
//        session.write("hello I am server");
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("sessionClosed");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("sessionOpen");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }
}