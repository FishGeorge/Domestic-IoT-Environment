import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;

public class Proto_AES_CBC_128 {
    /**
     * AES 是一种可逆加密算法，对用户的敏感信息加密处理
     * 加密用的Key 可以用26个字母和数字组成
     * 此处使用AES-128-CBC加密模式，key需要为16位
     * 加密输出base64编码以便于解密
     * <p>
     * （提供其他的加密输出，用于lumi局域网通信协议）
     */
    private String mKey = null;
    private String[] StrIVParameter = null;
    private byte[] BytIVPatameter = null;
    private Proto_AES_CBC_128 instance = null;

    //private static
    private Proto_AES_CBC_128(String[] IVPm, String KEY) {
        mKey = KEY;
        StrIVParameter = IVPm;
        BytIVPatameter = HexStr2byte(StrIVParameter);
    }

    private Proto_AES_CBC_128(String KEY) {
        mKey = KEY;
    }

    // 加密到base64
    public String Encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = mKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(BytIVPatameter);// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        return new BASE64Encoder().encode(encrypted);// 此处使用base64做转码。
//        return Arrays.toString(encrypted);
    }

    // 从base64解密
    public String Decrypt(String sSrc) {
        try {
            byte[] raw = mKey.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(BytIVPatameter);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    // 加密到十六进制数（大写）的String，用于lumi通信协议
    public String Encrypt2Hex(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = mKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(BytIVPatameter);// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] out_bytes = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        // 不知道为什么这里的bytes实际上有32项，lumi只取了前16项拓展为32个字符
//        System.out.println(out_bytes.length);
//        System.out.println(out_bytes[1]+"->"+Byte2Hex(out_bytes[1]));
        StringBuilder out_str = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            byte out_byte = out_bytes[i];
            out_str.append(Byte2Hex(out_byte));
        }
        return out_str.toString();
    }

    // 小于256的十六进制数的String数组转byte数组
    private byte[] HexStr2byte(String[] Hex) {
        byte[] out = new byte[Hex.length];
        for (int m = 0; m < (Hex.length); m++) {
            out[m] = (byte) Integer.parseInt(StrIVParameter[m].substring(StrIVParameter[m].length() - 2), 16);
        }
        return out;
    }

    // byte转十六进制数（大写）的String
    private String Byte2Hex(byte b) {
        String out = "";
        int i = Integer.parseInt(String.valueOf(b));
        if (i >= 0 && i < 16)
            out += "0";
        if (i < 0)
            i += 256;
        out += Integer.toHexString(i).toUpperCase();
//        System.out.println(String.valueOf(i)+"  "+i);
        return out;
    }

    public static void main(String[] args) throws Exception {
        // 提供一个Proto_AES_CBC_128使用方法的Demo
        // AES-CBC-128算法初始向量
        String[] IVpSample = new String[]{"0x17", "0x99", "0x6d", "0x09", "0x3d", "0x28", "0xdd", "0xb3", "0xba", "0x69", "0x5a", "0x2e", "0x6f", "0x58", "0x56", "0x2e"};
        // 算法使用的key
        String KeySample = "0987654321qwerty";
        System.out.println("加密使用的key：" + KeySample);
        // 算法操作对象
        Proto_AES_CBC_128 mAES = new Proto_AES_CBC_128(IVpSample, KeySample);
        // 需要加密的字串
        String TokenSample = "1234567890abcdef";
        System.out.println("明文是：" + TokenSample);

        // 加密
        String EnString = mAES.Encrypt(TokenSample);
        System.out.println("加密后的base64字串是：" + EnString);
        // 解密
        String DeString = mAES.Decrypt(EnString);
        System.out.println("解密后的明文是：" + DeString);
        // 加密输出byte[]
        EnString = mAES.Encrypt2Hex(TokenSample);
        System.out.println("加密后的lumi用字串是（只有前16项/32）：" + EnString);
    }
}

