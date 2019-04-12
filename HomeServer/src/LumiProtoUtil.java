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
    private LumiProtoSender
            Sender_findGateWay = null,
            Sender_getSubDevicesList = null,
            Sender_SinUnic = null;
//    private LumiProtoReceiver
//            Recver_MulMulc9898_1 = null,
//            Recver_SinUnic4321 = null,
//            Recver_SinUnic9898 = null;
//    public LumiProtoReceiver
//            Recver_MulMulc9898_2 = null;

    private String Gateway_sid = "";
    private String Gateway_token = "";
    private String Gateway_protoVer = "";
    private String Gateway_ip = "";

    private String[] IVparameter = {};
    private AES_CBC_128 aes_cbc_128 = null;
    private String lumiKey = "";// lumi通信协议密码，于米家App中查看
    private String writeKey = "";

    private ArrayList<LumiProtoDevice> SubDevices = new ArrayList<>();

    public LumiProtoUtil(String lumiKey) throws InterruptedException {
        this.lumiKey = lumiKey;
        this.IVparameter = new String[]{"0x17", "0x99", "0x6d", "0x09", "0x3d", "0x28", "0xdd", "0xb3", "0xba", "0x69", "0x5a", "0x2e", "0x6f", "0x58", "0x56", "0x2e"};
        init();
    }

    public LumiProtoUtil(String lumiKey, String[] IVparameter) throws InterruptedException {
        this.lumiKey = lumiKey;
        this.IVparameter = IVparameter;
        init();
    }

    public void init() throws InterruptedException {
        Sender_findGateWay = new LumiProtoSender(SinMulc, "{\"cmd\":\"whois\"}");
        Sender_getSubDevicesList = new LumiProtoSender(SinUnic, "{\"cmd\":\"get_id_list\"}");
        Sender_SinUnic = new LumiProtoSender(SinUnic);
//        Recver_MulMulc9898_1 = new LumiProtoReceiver(MulMulc, 9898);
//        Recver_MulMulc9898_2 = new LumiProtoReceiver(MulMulc, 9898);
//        Recver_SinUnic4321 = new LumiProtoReceiver(SinUnic, 4321);
//        Recver_SinUnic9898 = new LumiProtoReceiver(SinUnic, 9898);
        findGateWay();
        Thread.sleep(1000);
        getSubDevicesList();

        aes_cbc_128 = new AES_CBC_128(IVparameter, lumiKey);
        System.out.println(df.format(new Date()) + " Lumi局域网通信协议初始化成功。");
        System.out.println(df.format(new Date()) + " Protocol信息：\n" + this);

        Thread.sleep(1000);
        getGateWayToken();
    }

    @Override
    public String toString() {
        return "网关id: " + Gateway_sid +
                "\n网关ver: " + Gateway_protoVer +
                "\n网关ip: " + Gateway_ip +
                "\n设备列表:\n" + showSubDevices();
    }

    private void findGateWay() throws InterruptedException {
        LumiProtoReceiver Recver_SinUnic4321 = new LumiProtoReceiver(SinUnic, 4321);
        Recver_SinUnic4321.run();
        Sender_findGateWay.run();
        while (true) {
            Thread.sleep(250);
            if (!Recver_SinUnic4321.getRecvMsg().equals("null")) {
//                System.out.println("t-1");
                String Ans = Recver_SinUnic4321.getRecvMsg();
                Recver_SinUnic4321.resetMsg();
                JSONObject Ans_json = JSONObject.fromObject(Ans);
                Gateway_sid = Ans_json.getString("sid");
                Gateway_protoVer = Ans_json.getString("proto_version");
                Gateway_ip = Ans_json.getString("ip");
//                System.out.println("t0");
                break;
            }
        }
    }

    private void getSubDevicesList() throws InterruptedException {
        LumiProtoReceiver Recver_SinUnic9898 = new LumiProtoReceiver(SinUnic, 9898);
        Recver_SinUnic9898.run();
        Sender_getSubDevicesList.run();
        while (true) {
            Thread.sleep(250);
            if (!Recver_SinUnic9898.getRecvMsg().equals("null")) {
//                System.out.println("t1");
                String Ans = Recver_SinUnic9898.getRecvMsg();
                Recver_SinUnic9898.resetMsg();
                JSONObject Ans_json = JSONObject.fromObject(Ans);
                Ans = Ans_json.getString("data");
                String[] SubDevsid = Ans.substring(2, Ans.length() - 2).split("\",\"");
                for (int i = 0; i < SubDevsid.length; i++) {
                    String sid = SubDevsid[i];
                    Ans_json = readDevice(sid);
                    Thread.sleep(250);
//                    System.out.println("t4");
                    SubDevices.add(new LumiProtoDevice(Ans_json.getString("model"), sid, Ans_json.getString("short_id")));
//                    System.out.println("t2");
                }
                break;
            }
        }
    }

    private void getGateWayToken() {
        Thread RefreshToken = new Thread(new Runnable() {
            @Override
            public void run() {
                LumiProtoReceiver Recv_MulMulc9898 = new LumiProtoReceiver(MulMulc, 9898);
                Recv_MulMulc9898.run();
                while (true) {
//                    System.out.println("t8");
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Recv_MulMulc9898.getRecvHeartBMsg().contains("gateway")) {
//                        System.out.println("t5");
                        String msg = Recv_MulMulc9898.getRecvHeartBMsg();
                        if (msg.contains("gateway")) {
//                            System.out.println("t6");
                            try {
                                Thread.sleep(1000);
                                JSONObject HeartBMsgRecv_json = JSONObject.fromObject(msg);
                                Gateway_token = HeartBMsgRecv_json.getString("token");
                                if (!writeKey.equals(aes_cbc_128.Encrypt2Hex(Gateway_token))) {
                                    writeKey = aes_cbc_128.Encrypt2Hex(Gateway_token);
                                    System.out.println(df.format(new Date()) + " GateWayToken刷新，新的可用writeKey为: " + writeKey);
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

    public JSONObject readDevice(String sid) throws InterruptedException {
        Sender_SinUnic.setSendMsg("{\"cmd\":\"read\",\"sid\":\"" + sid + "\"}");
        LumiProtoReceiver Recver_SinUnic9898 = new LumiProtoReceiver(SinUnic, 9898);
        Recver_SinUnic9898.run();
        Sender_SinUnic.run();
        while (true) {
//            System.out.println("t3");
            Thread.sleep(100);
            if (!Recver_SinUnic9898.getRecvMsg().equals("null"))
                break;
        }
        String Ans = Recver_SinUnic9898.getRecvMsg();
//        Recver_SinUnic9898.resetMsg();
        return JSONObject.fromObject(Ans);
    }

    public JSONObject writeDevice(LumiProtoDevice dev, String data) {
        Sender_SinUnic.setSendMsg("{\"cmd\":\"write\"," +
                "\"model\":\"" + dev.getModel() + "\"," +
                "\"sid\":\"" + dev.getSid() + "\"," +
                "\"short_id\":" + dev.getShortid() + "\"," +
                "\"data\":\"" + data + "\"}");
        LumiProtoReceiver Recver_SinUnic9898 = new LumiProtoReceiver(SinUnic, 9898);
        Recver_SinUnic9898.run();
        Sender_SinUnic.run();
        return JSONObject.fromObject(Recver_SinUnic9898.getRecvMsg());
    }

//    public JSONObject ListenMulc() {
//        Recver_MulMulc9898_2.run();
//        return JSONObject.fromObject(Recver_MulMulc9898_2.getRecvMsg());
//    }

    public String showSubDevices() {
        StringBuilder stringB = new StringBuilder();
        for (LumiProtoDevice dev : SubDevices) {
            stringB.append(dev.toString()).append("\n");
        }
        return stringB.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        // Demo
        LumiProtoUtil lumiproto = new LumiProtoUtil("ntupng4kfjqrk5ad");
//        Thread.sleep(30000);
    }
}