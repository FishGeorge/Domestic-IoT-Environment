public class Proto_Device {
    private String model = "";
    private String sid = "";
    private String shortid = "";

    public Proto_Device(String model, String sid, String shortid) {
        this.model = model;
        this.sid = sid;
        this.shortid = shortid;
    }

    public String getModel() {
        return model;
    }

    public String getSid() {
        return sid;
    }

    public String getShortid() {
        return shortid;
    }

    @Override
    public String toString() {
        return "设备类型: " + model + " 设备id: " + sid;
    }
}