package lighthttpd;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author heweisheng software enginner
 */
public class ResponedHeader {       //简单的回复头部必要的内容

    private String vision = "HTTP/1.1";         //http版本，默认用1.1
    private String state;                       //状态
    private String date;                        //日期
    private String STATE[] = {"200 OK", "304 Not_Modified", "404 Not_found"};       //由于是简单的web服务器，所以只需要三个状态
    private String etag = null;
    private String content_length = null;
    private String server = "heweisheng";
    private String accept_ranges = "bytes";
    private String keep_alive = "timeout=5,max=100";
    private String content_type=null;
    private String HeadMes=null;

    public void setstate(int state) {
        this.state = STATE[state];
    }

    public void setdate() {
        Date date = new Date();        //创建日期对象   
        DateFormat df = DateFormat.getDateTimeInstance();
        this.date = date.toString();
    }

    public void setcontent(int length) {
        this.content_length = String.valueOf(length);
    }

    public void setcontent_type(String last)//RFC文档内容过多，只使用几个，有需要再根据RFC文档添加
    {
        String type = last.substring(last.lastIndexOf('.') + 1, last.length());
        System.out.println(type);
        if (type.equals("png") || type.equals("gif") || type.equals("img")) {
            this.content_type = "image/" + type;
        } else if (type.equals("html") || type.equals("txt")) {
            this.content_type = "text/" + type;
        } else if (type.equals("json")) {
            this.content_type = "application" + type;
        }
    }

    public void setetag(String etag) {
        this.etag = etag;
    }

    public String HeadtoString() {
        this.setdate();
        HeadMes = new String(this.vision + ' ' + state + "\r\n");
        HeadMes += "Date: " + this.date + "\r\n";
        HeadMes += "Server: " + this.server + "\r\n";
        if (etag != null) {
            HeadMes += "ETAG: " + this.etag + "\r\n";
        }
        HeadMes += "Accept-Ranges: " + this.accept_ranges + "\r\n";
        HeadMes += "Keep-Alive: " + this.keep_alive + "\r\n";
        if (this.content_length != null) {
            HeadMes += "Content-Length: " + this.content_length + "\r\n";
        }
        HeadMes += "Content-Type: " + this.content_type + "\r\n";
        return HeadMes;
    }
}
