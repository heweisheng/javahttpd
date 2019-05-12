package lighthttpd;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
/**
 * @author heweisheng
 * software enginner
 */
public class Getstatic {//静态的GET方法

    private Filedeal fd = new Filedeal();
    private static HashMap<String, String> fileetag = new HashMap();
    public int length(String str) {
        int i = 0;
        byte[] s = str.getBytes();
        return s.length;
    }

    public String getRandomCharacter(char ch1, char ch2) {
        String ret = "\"";
        for (int i = 0; i < 18; i++) {
            ret += (char) (ch1 + Math.random() * (ch2 - ch1 + 1));
        }
        return ret + "\"";
    }

    public boolean contorl(String URL, OutputStream out, File file, String etag) { //正确页面产生控制器
        String ETAG = null;
        ETAG = fileetag.get(URL);
        if (ETAG == null) //etag相同，发送304
        {
            String etagnew = new String(getRandomCharacter('a', 'z'));
            fileetag.put(URL, etagnew);
            return getfile200(URL, out, file, etagnew);
        } else if (etag == null || !ETAG.equals(etag)) {
            return getfile200(URL, out, file, ETAG);
        } else {
            return getfile304(URL, out, ETAG);
        }

    }

    public boolean getfile200(String URL, OutputStream out, File file, String etag) {
        try {
            ResponedHeader headspond = new ResponedHeader();
            headspond.setstate(0);
            headspond.setcontent_type(URL);
            headspond.setetag(etag);
            headspond.setcontent(fd.getFileSize1(file));
            String rethead = headspond.HeadtoString();
            rethead += "\r\n";
            byte[] tobyte = rethead.getBytes();
            out.write(tobyte);
            fd.Sendfile(out, file);
            out.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public boolean getfile304(String URL, OutputStream out, String etag) {
        try {
            ResponedHeader headspond = new ResponedHeader();
            headspond.setstate(1);
            headspond.setcontent_type(URL);
            headspond.setetag(etag);
            String rethead = headspond.HeadtoString();
            rethead += "\r\n";
            byte[] tobyte = rethead.getBytes();
            out.write(tobyte);
            out.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public boolean getfile404(OutputStream out, String etag) {       //错误页面
        String ETAG = fileetag.get("./404NOTFOUND.html\0");
        if (ETAG == null) {
            ETAG = new String(getRandomCharacter('a', 'z'));
            fileetag.put("./404NOTFOUND.html\0", ETAG);
        }
        try {
            if (!ETAG.equals(etag)) {
                ResponedHeader headspond = new ResponedHeader();
                headspond.setstate(0);
                headspond.setetag(ETAG);
                headspond.setcontent_type("./404NOTFOUND.html\0");
                String html404 = "<!DOCTYPE html>\n"
                        + "<head>\n"
                        + "    <title>404 NOT FOUND</title>\n"
                        + "    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "    <p>服务器目录下不存在该内容</p>\n"
                        + "</body>";
                headspond.setcontent(this.length(html404));
                String rethead = headspond.HeadtoString();
                rethead += "\r\n";
                byte[] tobyte = rethead.getBytes();
                out.write(tobyte);
                tobyte = html404.getBytes();
                out.write(tobyte);
                out.close();
            } else {
                return getfile304("./404NOTFOUND.html\0",out,etag);
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

}
