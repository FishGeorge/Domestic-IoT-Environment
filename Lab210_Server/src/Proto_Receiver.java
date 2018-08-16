import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Proto_Receiver {
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

    public Proto_Receiver(int type, int port) {
        receive_type = type;
        Port = port;
    }

    public void ResetMsg() {
        message = "null";
        heartbeat_msg = "null";
    }

    private void Unicast_Receive() throws Exception {
        packet = new DatagramPacket(new byte[length], length);
        mcSocket = new MulticastSocket(Port);

        System.out.println(df.format(new Date()) + " [=>Receiver] UniR starts, running at: " + IpAddress + "/" + Port);

//        mcSocket.close();
    }

    private void Multicast_Receive() throws Exception {
        packet = new DatagramPacket(new byte[length], length);
        mcSocket = new MulticastSocket(Port);
        IpAddr = InetAddress.getByName(IpAddress);
        mcSocket.joinGroup(IpAddr);

        System.out.println(df.format(new Date()) + " [=>Receiver] MulR starts, running at: " + IpAddress + "/" + Port);

//        mcSocket.leaveGroup(IpAddr);
//        mcSocket.close();
    }

    public String GetRecvMsg() {
        return message;
    }

    public String GetRecvHeartBMsg() {
        return heartbeat_msg;
    }

    public void Run() {
        Thread receive = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (receive_type) {
                        case 0:
                            Unicast_Receive();
                            mcSocket.receive(packet);
                            message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                            System.out.println(df.format(new Date()) + " [=>Receiver] Unicast msg:" + message);

                            mcSocket.close();
                            System.out.println(df.format(new Date()) + " [=>Receiver] UniR ends.");
                            break;
                        case 1:
                            Unicast_Receive();
                            while (true) {
                                mcSocket.receive(packet);
                                message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                                System.out.println(df.format(new Date()) + " [=>Receiver] Unicast msg:" + message);
                            }
                        case 2:
                            Multicast_Receive();
                            mcSocket.receive(packet);
                            message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                            if (message.contains("heartbeat"))
                                heartbeat_msg = message;
                            System.out.println(df.format(new Date()) + " [=>Receiver] Multicast msg:" + message);

                            mcSocket.leaveGroup(IpAddr);
                            mcSocket.close();
                            System.out.println(df.format(new Date()) + " [=>Receiver] MulR ends.");
                            break;
                        case 3:
                            Multicast_Receive();
                            while (true) {
                                mcSocket.receive(packet);
                                message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                                if (message.contains("heartbeat")) {
//                                    System.out.println("t7");
                                    heartbeat_msg = message;
                                }
                                System.out.println(df.format(new Date()) + " [=>Receiver] Multicast msg:" + message);
                            }
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        receive.start();
    }
}