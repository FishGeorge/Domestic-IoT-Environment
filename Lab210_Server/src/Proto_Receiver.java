import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Proto_Receiver {
    private Date day = null;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String IpAddress = "224.0.0.50";
    private InetAddress IpAddr = null;
    private int Port;

    private int receive_type;
    private String message = null;
    private int length = 1000;

    private DatagramPacket packet = null;

    private MulticastSocket mcSocket = null;

    public Proto_Receiver(int type, int port) {
        receive_type = type;
        Port = port;
    }

    public void Multicast_Receive() throws Exception {
        packet = new DatagramPacket(new byte[length], length);
        mcSocket = new MulticastSocket(Port);
        IpAddr = InetAddress.getByName(IpAddress);
        mcSocket.joinGroup(IpAddr);

        day = new Date();
        System.out.println(df.format(day) + " [=>Receiver] McR starts, running at:" + mcSocket.getInetAddress());

//        mcSocket.leaveGroup(IpAddr);
//        mcSocket.close();
    }

    public void Unicast_Receive() throws Exception {
        packet = new DatagramPacket(new byte[length], length);
        mcSocket = new MulticastSocket(Port);
        IpAddr = InetAddress.getByName(IpAddress);

        day = new Date();
        System.out.println(df.format(day) + " [=>Receiver] UnR starts, running at:" + mcSocket.getInetAddress());

//        mcSocket.close();
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
                            day = new Date();
                            System.out.println(df.format(day) + " [=>Receiver] Unicast msg:" + message);

                            mcSocket.close();
                            day = new Date();
                            System.out.println(df.format(day) + " [=>Receiver] UnR ends.");
                            break;
                        case 1:
                            Multicast_Receive();
                            mcSocket.receive(packet);
                            message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                            day = new Date();
                            System.out.println(df.format(day) + " [=>Receiver] Multicast msg:" + message);

                            mcSocket.leaveGroup(IpAddr);
                            mcSocket.close();
                            day = new Date();
                            System.out.println(df.format(day) + " [=>Receiver] McR ends.");
                            break;
                        case 2:
                            Multicast_Receive();
                            while (true) {
                                mcSocket.receive(packet);
                                message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                                day = new Date();
                                System.out.println(df.format(day) + " [=>Receiver] Multicast msg:" + message);
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
