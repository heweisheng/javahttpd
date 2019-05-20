package lighthttpd;

/**
 * @author heweisheng
 * software enginner
 */
public class GetHeader {            //由于是微形的web服务器，所以只用关心四个字段就可以了 本来想做Keep-alive时间太短，先不做了

    private String function=null;
    private String httpvs=null;
    private String url=null;
    private String clietag=null;
    private String useragent=null;
    private String clength=null;  
    private String boundary=null;
    private String ctype=null;
    public String getetag()
    {
        return this.clietag;
    }
    public int getlength()
    {
        return Integer.parseInt(this.clength);
    }
    public String getuseragent()
    {
        return this.useragent;
    }
    public String getboundary()
    {
        return this.boundary;
    }
    public void Readheader(String request) {/*第一行比较特殊，有三个元素，不是key value形式*/
        int head = 0;
        int end = head;
        while (request.charAt(end) != ' ') {
            end++;
        }
        this.function = request.substring(head, end);
        head = end = end + 1;
        while (request.charAt(end) != ' ') {
            end++;
        }
        this.url = request.substring(head, end);
        head = end = end + 1;
        while (request.charAt(end) != '\r') {
            end++;
        }
        this.httpvs = request.substring(head, end);
        head = end = end + 2;//+2是因为http报文是/r/n为一个头结束      
        String dict[] = {"If-None-Match", "User-Agent", "Content-Length","Content-Type"};       
        int length=request.length();
        for (int i = head; i < length; i++) {
            String key = new String();
            String value = new String();
            while (i<length&&request.charAt(i) != ':') {
                i++;
            }
            //key
            key = request.substring(head, i);
            head += key.length() + 2;
            //value
            while (i<length&&request.charAt(i) != '\r') i++;               
            value = request.substring(head, i);
            for (int j = 0; j < dict.length; j++) {                              
                if (key.equals(dict[j])) {          
                    switch (j)
                    {
                            case 0:
                                this.clietag=new String(value); 
                                break;
                            case 1:
                                this.useragent=new String(value);
                                break;
                            case 2:
                                this.clength=new String(value);
                                break;
                            case 3:
                                this.ctype=new String(value);
                                if(ctype.contains("multipart/form-data"))
                                    this.boundary=new String(value.substring(value.indexOf("boundary")+"boundary=".length()));
                                break;
                            default:
                                break;
                    }
                    break;                    
                }
            }
            head = i + 2;
            if(request.charAt(head)=='\r')
                break;
        }
    }

    public void setfuncion(String function) {
        this.function = function;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public String geturl() {
        return this.url;
    }

    public String getfunction() {
        return this.function;
    }
}
