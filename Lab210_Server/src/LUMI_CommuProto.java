public class LUMI_CommuProto {
    static final int SinUnic = 0, MulUnic = 1, SinMulc = 2, MulMulc = 3;

    public static void main(String args[]) {
        // 测试
        // 1) 接收器循环接受组播
//        new Proto_Receiver(MulMulc,9898).Run();
        // 2) 接收器单次接受组播
//        new Proto_Receiver(SinMulc,9898).Run();
        // 3) 接收器单次接受单播（配合发送器）
        // i) 发送器单次发送组播
//        new Proto_Sender(SinMulc).Run();
//        new Proto_Receiver(SinUnic, 4321).Run();
        // ii) 发送器单次发送单播
        new Proto_Sender(SinUnic, "{\"cmd\":\"get_id_list\"}").Run();
        new Proto_Receiver(SinUnic, 9898).Run();
    }
}
