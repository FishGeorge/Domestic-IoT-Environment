import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Test_Receiver {
    public static void Multicast_Receive() throws Exception {
        int mcPort = 4321;
        String mcIPStr = "224.0.0.50";
        MulticastSocket mcSocket = new MulticastSocket(mcPort);
        InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
        System.out.println("Multicast Receiver running at:" + mcSocket.getLocalSocketAddress());

        mcSocket.joinGroup(mcIPAddress);

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

    public static void main(String[] args){
        try {
            Multicast_Receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
