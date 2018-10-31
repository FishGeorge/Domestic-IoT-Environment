public class ProtoDevice {
    private String model = "";
    private String sid = "";
    private String shortid = "";

    public ProtoDevice(String model, String sid, String shortid) {
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
        return "设备type: " + model + " 设备id: " + sid + " 设备short id: " + shortid;
    }
}