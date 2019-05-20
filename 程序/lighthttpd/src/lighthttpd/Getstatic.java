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
        if (ETAG == null) //服务器散列表不存在，先生成etag，再返回页面
        {
            String etagnew = new String(getRandomCharacter('a', 'z'));
            fileetag.put(URL, etagnew);
            return getfile200(URL, out, file, etagnew);
        } else if (etag == null || !ETAG.equals(etag)) {//etag为空或者etag不相同产生新页面。
            return getfile200(URL, out, file, ETAG);
        } else {//etag相同，发送304
            return getfile304(URL, out, ETAG);
        }

    }

    public boolean getfile200(String URL, OutputStream out, File file, String etag) {//200 生成页面
        try {
            ResponedHeader headspond = new ResponedHeader();
            headspond.setstate(0);//设置状态码200
            headspond.setcontent_type(URL);//设置rfc文件类型
            headspond.setetag(etag);//设置etag
            headspond.setcontent(fd.getFileSize1(file));//获取文件大小
            String rethead = headspond.HeadtoString();
            rethead += "\r\n";//报文结尾
            byte[] tobyte = rethead.getBytes();//生成报文头
            out.write(tobyte);//先写报文头
            fd.Sendfile(out, file);//发送文件
            out.close();//关闭流
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
            rethead += "\r\n";//同200，但是只要生成保文头就够了
            byte[] tobyte = rethead.getBytes();
            out.write(tobyte);
            out.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public boolean getfile404(OutputStream out, String etag) {       //错误页面，懒，直接写在代码里，其实可以直接返回404状态，但是很多服务器都是自定义404页面。
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
