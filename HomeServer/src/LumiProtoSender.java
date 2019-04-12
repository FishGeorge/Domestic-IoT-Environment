import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LumiProtoSender {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private InetAddress IpAddr = null;
    private int Port;

    private int send_type;
    private String message = null;
    private int interval = 5000;

    private byte[] msg_byte;
    private DatagramPacket packet = null;
    private MulticastSocket udpSocket = null;

//    private DBManager dbManager = new DBManager();

    public LumiProtoSender(int type) {
        send_type = type;
    }

    public LumiProtoSender(int type, String m) {
        send_type = type;
        message = m;
        msg_byte = message.getBytes();
    }

    public LumiProtoSender(int type, String m, int time) {
        send_type = type;
        message = m;
        msg_byte = message.getBytes();
        interval = time;
    }

    public void setSendMsg(String msg) {
        message = msg;
        msg_byte = message.getBytes();
    }

    private void unicastSend() throws Exception {
        IpAddr = InetAddress.getByName("224.0.0.50");
        Port = 9898;
        udpSocket = new MulticastSocket(Port);
//        udpSocket.joinGroup(IpAddr);

        packet = new DatagramPacket(msg_byte, msg_byte.length);
        packet.setAddress(IpAddr);
        packet.setPort(Port);

        udpSocket.send(packet);
        String dfTime = df.format(new Date());
        // 数据库操作
        // dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "send", "224.0.0.50/9898", message);
        System.out.println(dfTime + " [<=Sender] Unicast msg to 224.0.0.50/9898: " + message);

        udpSocket.close();
    }

    private void multicastSend() throws Exception {
        String msg = "{\"cmd\":\"whois\"}";
        msg_byte = msg.getBytes();

        IpAddr = InetAddress.getByName("224.0.0.50");
        Port = 4321;
        udpSocket = new MulticastSocket(Port);
        udpSocket.joinGroup(IpAddr);

        packet = new DatagramPacket(msg_byte, msg_byte.length);
        packet.setAddress(IpAddr);
        packet.setPort(Port);

        udpSocket.send(packet);
        String dfTime = df.format(new Date());
        // 数据库操作
        // dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "send", "224.0.0.50/4321", msg);
        System.out.println(dfTime + " [<=Sender] Multicast msg to 224.0.0.50/4321: " + msg);

        udpSocket.close();
    }

    public void run() {
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (send_type) {
                        case 0:
                            unicastSend();
                            break;
                        case 1:
                            while (true) {
                                unicastSend();
                                Thread.sleep(interval);
                            }
                        case 2:
                            multicastSend();
                            break;
                        case 3:
                            while (true) {
                                multicastSend();
                                Thread.sleep(interval);
                            }
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
    }
}