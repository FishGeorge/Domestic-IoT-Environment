public class HomeSensorManager {
    private static LumiProtoUtil lumiProto = null;
    private static String lumiKey = "ntupng4kfjqrk5ad";


    public static void main(String args[]) {
        try {
            lumiProto = new LumiProtoUtil(lumiKey);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}