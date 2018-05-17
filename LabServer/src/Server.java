import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.*;
import java.net.*;

public class Server {
    public static ClientAccountDB clientAccountDB;
    public static DateEventDB dateEventDB;

    public static void main(String[] args) {
        // 服务器初始化
        // 帐户数据库准备
        clientAccountDB = new ClientAccountDB();
        clientAccountDB.AccountDB_Create();
        // 服务器循环运行
        NioSocketAcceptor acceptor = null;
        try {
            acceptor = new NioSocketAcceptor();
            acceptor.setHandler(new ServerHandler());
            acceptor.getFilterChain().addLast("mFilter", new ProtocolCodecFilter(new TextLineCodecFactory()));
            acceptor.setReuseAddress(true);
            acceptor.bind(new InetSocketAddress(8989));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
