import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Date;

public class MulticastProtocol {
    public static void Multicast_Send() throws Exception {
        String msg = "{\"cmd\":\"whois\"}";
//        String msg = "test";
        byte[] msg_byte = msg.getBytes();

        InetAddress mcIPAddress = InetAddress.getByName("224.0.0.50");
        int mcPort = 4321;
        MulticastSocket udpSocket = new MulticastSocket(mcPort);
        DatagramPacket packet = new DatagramPacket(msg_byte, msg_byte.length);
        packet.setAddress(mcIPAddress);
        packet.setPort(mcPort);

//        udpSocket.setTimeToLive(1);
        udpSocket.joinGroup(mcIPAddress);
        udpSocket.send(packet);

        System.out.println("Send a multicast message: " + msg);

        udpSocket.close();
    }

    public static void Multicast_Receive() throws Exception {
        int mcPort = 4321;
        String mcIPStr = "224.0.0.50";
        MulticastSocket mcSocket = new MulticastSocket(mcPort);
        InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);

        mcSocket.joinGroup(mcIPAddress);
        System.out.println("Multicast Receiver running at:" + mcSocket.getLocalSocketAddress());

        DatagramPacket packet = new DatagramPacket(new byte[500], 500);

        System.out.println("Waiting for a multicast message...");
        while (true) {
            mcSocket.receive(packet);
            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
            System.out.println("[Multicast  Receiver] Received:" + msg);
        }
//        mcSocket.leaveGroup(mcIPAddress);
//        mcSocket.close();
    }

    public static void Receive() throws Exception {
        int port = 9898;
        byte[] msg_byte = new byte[500];
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(msg_byte, msg_byte.length);

        System.out.println("Waiting for a message...");
        socket.receive(packet);
        String ip = packet.getAddress().getHostAddress();
        String msg = new String(packet.getData(), 0, packet.getLength());

        System.out.println("Received from " + ip + ":" + msg);

    }

    /**
     * main方法描述:主函数测试
     */
    public static void main(String[] args) {
        try {
            Thread send = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Multicast_Send();
                            Thread.sleep(3000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Thread receive = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
//                            Receive();//0802 发现网关回应呼唤信息是通过组播回答的，不是说明所说的单播。待确认
                            Multicast_Receive();
                            //0805 确定米家空调伴侣暂不支持局域网网关协议
//                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receive.start();
            Thread.sleep(1000);
            send.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}