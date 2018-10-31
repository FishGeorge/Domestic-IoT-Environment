public class SensorsRecordingSystem {
    private static LumiProtoUtil lumiProto = null;
    private static String LumiKey = "ntupng4kfjqrk5ad";
    private static DBManager dbManager = new DBManager();

    public static void main(String args[]) {
        try {
            lumiProto = new LumiProtoUtil(LumiKey);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}