import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProtoSender {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private InetAddress IpAddr = null;
    private int Port;

    private int send_type;
    private String message = null;
    private int interval = 5000;

    private byte[] msg_byte;
    private DatagramPacket packet = null;
    private MulticastSocket udpSocket = null;

    private DBManager dbManager = new DBManager();

    public ProtoSender(int type) {
        send_type = type;
    }

    public ProtoSender(int type, String m) {
        send_type = type;
        message = m;
        msg_byte = message.getBytes();
    }

    public void SetSendMsg(String msg) {
        message = msg;
        msg_byte = message.getBytes();
    }

    public ProtoSender(int type, String m, int time) {
        send_type = type;
        message = m;
        msg_byte = message.getBytes();
        interval = time;
    }

    private void Unicast_Send() throws Exception {
        IpAddr = InetAddress.getByName("224.0.0.50");
        Port = 9898;
        udpSocket = new MulticastSocket(Port);
//        udpSocket.joinGroup(IpAddr);

        packet = new DatagramPacket(msg_byte, msg_byte.length);
        packet.setAddress(IpAddr);
        packet.setPort(Port);

        udpSocket.send(packet);
        String dfTime = df.format(new Date());
        dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "send", "224.0.0.50/9898", message);
        System.out.println(dfTime + " [<=Sender] Unicast msg to 224.0.0.50/9898: " + message);

        udpSocket.close();
    }

    private void Multicast_Send() throws Exception {
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
        dbManager.Insert_CommRecord(dfTime.split(" ")[0], dfTime.split(" ")[1], "send", "224.0.0.50/4321", msg);
        System.out.println(dfTime + " [<=Sender] Multicast msg to 224.0.0.50/4321: " + msg);

        udpSocket.close();
    }

    public void Run() {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (send_type) {
                        case 0:
                            Unicast_Send();
                            break;
                        case 1:
                            while (true) {
                                Unicast_Send();
                                Thread.sleep(interval);
                            }
                        case 2:
                            Multicast_Send();
                            break;
                        case 3:
                            while (true) {
                                Multicast_Send();
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
        send.start();
    }
}