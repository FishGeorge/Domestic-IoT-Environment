import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LumiProtoReceiver {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String IpAddress = "224.0.0.50";
    private InetAddress IpAddr = null;
    private int Port;

    private int receive_type;
    private String message = "null";
    private String heartbeat_msg = "null";
    private int length = 1000;

    private DatagramPacket packet = null;
    private MulticastSocket mcSocket = null;

//    private DBManager dbManager=new DBManager();

    public LumiProtoReceiver(int type, int port) {
        receive_type = type;
        Port = port;
    }

    public void resetMsg() {
        message = "null";
        heartbeat_msg = "null";
    }

    private void unicastReceive() throws Exception {
        packet = new DatagramPacket(new byte[length], length);
        mcSocket = new MulticastSocket(Port);

        System.out.println(df.format(new Date()) + " [=>Receiver] UniR starts, running at: " + IpAddress + "/" + Port);

//        mcSocket.close();
    }

    private void multicastReceive() throws Exception {
        packet = new DatagramPacket(new byte[length], length);
        mcSocket = new MulticastSocket(Port);
        IpAddr = InetAddress.getByName(IpAddress);
        mcSocket.joinGroup(IpAddr);

        System.out.println(df.format(new Date()) + " [=>Receiver] MulR starts, running at: " + IpAddress + "/" + Port);

//        mcSocket.leaveGroup(IpAddr);
//        mcSocket.close();
    }

    public String getRecvMsg() {
        return message;
    }

    public String getRecvHeartBMsg() {
        return heartbeat_msg;
    }

    public void run() {
        Thread recvThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (receive_type) {
                        case 0:
                            unicastReceive();
                            mcSocket.receive(packet);
                            message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                            String dfTime = df.format(new Date());
                            // 数据库操作
                            // dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "receive", "", message);
                            System.out.println(dfTime + " [=>Receiver] Unicast msg: " + message);

                            mcSocket.close();
                            System.out.println(df.format(new Date()) + " [=>Receiver] UniR ends.");
                            break;
                        case 1:
                            unicastReceive();
                            while (true) {
                                mcSocket.receive(packet);
                                message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                                dfTime = df.format(new Date());
                                // 数据库操作
                                // dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "receive", "", message);
                                System.out.println(dfTime + " [=>Receiver] Unicast msg: " + message);
                            }
                        case 2:
                            multicastReceive();
                            mcSocket.receive(packet);
                            message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                            if (message.contains("heartbeat"))
                                heartbeat_msg = message;
                            dfTime = df.format(new Date());
                            // 数据库操作
                            // dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "receive", "", message);
                            System.out.println(dfTime + " [=>Receiver] Multicast msg: " + message);

                            mcSocket.leaveGroup(IpAddr);
                            mcSocket.close();
                            System.out.println(df.format(new Date()) + " [=>Receiver] MulR ends.");
                            break;
                        case 3:
                            multicastReceive();
                            while (true) {
                                mcSocket.receive(packet);
                                message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                                if (message.contains("heartbeat")) {
//                                    System.out.println("t7");
                                    heartbeat_msg = message;
                                }
                                dfTime = df.format(new Date());
                                // 数据库操作
                                // dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "receive", "", message);
                                System.out.println(dfTime + " [=>Receiver] Multicast msg: " + message);
                            }
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        recvThread.start();
    }
}