import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Proto_Sender {
    private Date day = null;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private InetAddress IpAddr = null;
    private int Port;

    private int send_type;
    private String message = null;
    private int interval = 0;

    private byte[] msg_byte;
    private DatagramPacket packet = null;

    private MulticastSocket udpSocket = null;

    public Proto_Sender(int type) {
        send_type = type;
    }

    public Proto_Sender(int type, String m) {
        send_type = type;
        message = m;
        msg_byte = message.getBytes();
    }

    public Proto_Sender(int type, String m, int time) {
        send_type = type;
        message = m;
        msg_byte = message.getBytes();
        interval = time;
    }

    private void Multicast_Send() throws Exception {
        String msg = "{\"cmd\":\"whois\"}";
        msg_byte = msg.getBytes();

        IpAddr = InetAddress.getByName("224.0.0.50");
        Port = 4321;
        udpSocket = new MulticastSocket(Port);
        udpSocket.joinGroup(IpAddr);

        packet = new DatagramPacket(msg_byte, msg_byte.length);
//        packet.setAddress(IpAddr);
//        packet.setPort(Port);

        udpSocket.send(packet);
        day = new Date();
        System.out.println(df.format(day) + " [<=Sender] Multicast msg: " + msg);

        udpSocket.close();
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
        day = new Date();
        System.out.println(df.format(day) + " [<=Sender] Unicast msg: " + message);

        udpSocket.close();
    }

    public void Run() {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (send_type) {
                        case 0:
                            Multicast_Send();
                            break;
                        case 1:
                            Unicast_Send();
                            break;
                        case 2:
                            while (true) {
                                Unicast_Send();
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
    }
}