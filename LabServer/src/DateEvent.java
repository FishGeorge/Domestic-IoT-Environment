import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by 37280 on 2017/12/29.
 *
 * @author GeOrange
 * @version ${version}
 */

public class DateEvent implements Serializable {
    private String title;
    private int Date;
    private int[] period;
    private String Content;
    private String location;
    private String remark;

    public DateEvent() {
    }

    public DateEvent(String t, int[] p) {
        setTitle(t);
        setPeriod(p);
    }

    public DateEvent(HashMap<Integer, String> p) {
        setData_HashMap(p);
    }

    // 原始信息->HashMap封装
    public HashMap<Integer, String> getDateEvent_HashMap() {
        HashMap<Integer, String> out = new HashMap<Integer, String>();
        out.put(0, getTitle());
        out.put(1, String.valueOf(getDate()));
        out.put(2, String.valueOf(getPeriod()[0]) + String.valueOf(getPeriod()[1]));
        out.put(3, getContent());
        out.put(4, getLocation());
        out.put(5, getRemark());
        return out;
    }

    // HashMap封装->原始信息
    public void setData_HashMap(HashMap<Integer, String> p) {
        setTitle(p.get(0));
        setDate(Integer.parseInt(p.get(1)));
        setPeriod(new int[]{Integer.parseInt(p.get(2)) / 10000, Integer.parseInt(p.get(2)) % 10000});
        setContent(p.get(3));
        setLocation(p.get(4));
        setRemark(p.get(5));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDate() {
        return Date;
    }

    public void setDate(int date) {
        Date = date;
    }

    public int[] getPeriod() {
        return period;
    }

    public void setPeriod(int[] period) {
        this.period = period;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
