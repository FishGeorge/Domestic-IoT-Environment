import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * LumiProtoUtil
 *
 *
 */
public class LumiProtoUtil {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int SinUnic = 0, MulUnic = 1, SinMulc = 2, MulMulc = 3;
    private ProtoSender
            Send_DiscGW = null,
            Send_QueSubD = null,
            Send_SinUnic = null;
//    private ProtoReceiver
//            Recv_MulMulc9898_1 = null,
//            Recv_SinUnic4321 = null,
//            Recv_SinUnic9898 = null;
//    public ProtoReceiver
//            Recv_MulMulc9898_2 = null;

    private String Gateway_sid = "";
    private String Gateway_token = "";
    private String Gateway_protoVer = "";
    private String Gateway_ip = "";

    private static final String[]
            IVparameter = new String[]{"0x17", "0x99", "0x6d", "0x09", "0x3d", "0x28", "0xdd", "0xb3", "0xba", "0x69", "0x5a", "0x2e", "0x6f", "0x58", "0x56", "0x2e"};
    private static String Key = "";// lumi通信协议密码，于米家App中查看
    private AES_CBC_128 Aes = null;
    private String WriteKEY = "null";

    private ArrayList<ProtoDevice> SubDevices = new ArrayList<>();

    public LumiProtoUtil(String LumiKey) throws InterruptedException {
        this.Init(LumiKey);
    }

    public void Init(String LumiKey) throws InterruptedException {
        Send_DiscGW = new ProtoSender(SinMulc, "{\"cmd\":\"whois\"}");
        Send_QueSubD = new ProtoSender(SinUnic, "{\"cmd\":\"get_id_list\"}");
        Send_SinUnic = new ProtoSender(SinUnic);
//        Recv_MulMulc9898_1 = new ProtoReceiver(MulMulc, 9898);
//        Recv_MulMulc9898_2 = new ProtoReceiver(MulMulc, 9898);
//        Recv_SinUnic4321 = new ProtoReceiver(SinUnic, 4321);
//        Recv_SinUnic9898 = new ProtoReceiver(SinUnic, 9898);
        DiscGateway();
        Thread.sleep(1000);
        QueSubDevices();

        Key = LumiKey;
        Aes = new AES_CBC_128(IVparameter, Key);
        System.out.println(df.format(new Date()) + " Lumi局域网通信协议初始化成功。");
        System.out.println(df.format(new Date()) + " Protocol信息：\n" + this);

        Thread.sleep(1000);
        GetGWtoken();
    }

    @Override
    public String toString() {
        return "网关id: " + Gateway_sid +
                "\n网关ver: " + Gateway_protoVer +
                "\n网关ip: " + Gateway_ip +
                "\n" + ShowSubDevices();
    }

    private void DiscGateway() throws InterruptedException {
        ProtoReceiver Recv_SinUnic4321 = new ProtoReceiver(SinUnic, 4321);
        Recv_SinUnic4321.Run();
        Send_DiscGW.Run();
        while (true) {
            Thread.sleep(250);
            if (!Recv_SinUnic4321.GetRecvMsg().equals("null")) {
//                System.out.println("test");
                String Ans = Recv_SinUnic4321.GetRecvMsg();
                Recv_SinUnic4321.ResetMsg();
                JSONObject Ans_json = JSONObject.fromObject(Ans);
                Gateway_sid = Ans_json.getString("sid");
                Gateway_protoVer = Ans_json.getString("proto_version");
                Gateway_ip = Ans_json.getString("ip");
//                System.out.println("test");
                break;
            }
        }
    }

    private void QueSubDevices() throws InterruptedException {
        ProtoReceiver Recv_SinUnic9898 = new ProtoReceiver(SinUnic, 9898);
        Recv_SinUnic9898.Run();
        Send_QueSubD.Run();
        while (true) {
            Thread.sleep(250);
            if (!Recv_SinUnic9898.GetRecvMsg().equals("null")) {
//                System.out.println("t1");
                String Ans = Recv_SinUnic9898.GetRecvMsg();
                Recv_SinUnic9898.ResetMsg();
                JSONObject Ans_json = JSONObject.fromObject(Ans);
                Ans = Ans_json.getString("data");
                String[] SubDevsid = Ans.substring(2, Ans.length() - 2).split("\",\"");
                for (int i = 0; i < SubDevsid.length; i++) {
                    String sid = SubDevsid[i];
                    Ans_json = ReadDevice(sid);
                    Thread.sleep(250);
//                    System.out.println("t4");
                    SubDevices.add(new ProtoDevice(Ans_json.getString("model"), sid, Ans_json.getString("short_id")));
//                    System.out.println("t2");
                }
                break;
            }
        }
    }

    private void GetGWtoken() {
        Thread RefreshToken = new Thread(new Runnable() {
            @Override
            public void run() {
                ProtoReceiver Recv_MulMulc9898 = new ProtoReceiver(MulMulc, 9898);
                Recv_MulMulc9898.Run();
                while (true) {
//                    System.out.println("t8");
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Recv_MulMulc9898.GetRecvHeartBMsg().contains("gateway")) {
//                        System.out.println("t5");
                        String msg = Recv_MulMulc9898.GetRecvHeartBMsg();
                        if (msg.contains("gateway")) {
//                            System.out.println("t6");
                            try {
                                Thread.sleep(1000);
                                JSONObject HeartBMsgRecv_json = JSONObject.fromObject(msg);
                                Gateway_token = HeartBMsgRecv_json.getString("token");
                                if (!WriteKEY.equals(Aes.Encrypt2Hex(Gateway_token))) {
                                    WriteKEY = Aes.Encrypt2Hex(Gateway_token);
                                    System.out.println(df.format(new Date()) + " GWtoken刷新，加密后可用KEY: " + WriteKEY);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        RefreshToken.run();
    }

    public JSONObject ReadDevice(String sid) throws InterruptedException {
        Send_SinUnic.SetSendMsg("{\"cmd\":\"read\",\"sid\":\"" + sid + "\"}");
        ProtoReceiver Recv_SinUnic9898 = new ProtoReceiver(SinUnic, 9898);
        Recv_SinUnic9898.Run();
        Send_SinUnic.Run();
        while (true) {
//            System.out.println("t3");
            Thread.sleep(100);
            if (!Recv_SinUnic9898.GetRecvMsg().equals("null"))
                break;
        }
        String Ans = Recv_SinUnic9898.GetRecvMsg();
//        Recv_SinUnic9898.ResetMsg();
        return JSONObject.fromObject(Ans);
    }

    public JSONObject WriteDevice(ProtoDevice dev, String data) {
        Send_SinUnic.SetSendMsg("{\"cmd\":\"write\"," +
                "\"model\":\"" + dev.getModel() + "\"," +
                "\"sid\":\"" + dev.getSid() + "\"," +
                "\"short_id\":" + dev.getShortid() + "\"," +
                "\"data\":\"" + data + "\"}");
        ProtoReceiver Recv_SinUnic9898 = new ProtoReceiver(SinUnic, 9898);
        Recv_SinUnic9898.Run();
        Send_SinUnic.Run();
        return JSONObject.fromObject(Recv_SinUnic9898.GetRecvMsg());
    }

//    public JSONObject ListenMulc() {
//        Recv_MulMulc9898_2.Run();
//        return JSONObject.fromObject(Recv_MulMulc9898_2.GetRecvMsg());
//    }

    public String ShowSubDevices() {
        StringBuilder stringB = new StringBuilder();
        for (ProtoDevice dev : SubDevices) {
            stringB.append(dev.toString()).append("\n");
        }
        return stringB.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        LumiProtoUtil lumiproto = new LumiProtoUtil("ntupng4kfjqrk5ad");
//        Thread.sleep(30000);
    }
}