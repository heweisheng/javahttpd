package lighthttpd;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import sun.misc.BASE64Decoder;

/**
 * @author heweisheng software enginner
 */
public class Dynamic {

    private GetHeader report = null;
    private String msg = null;
    private OutputStream out = null;
    private InputStream in = null;
    private final Filedeal fs = new Filedeal();
    private final Getstatic notfound = new Getstatic();
    private final String save;

    public String getRandomCharacter(char ch1, char ch2) {
        String ret = "";
        for (int i = 0; i < 18; i++) {
            ret += (char) (ch1 + Math.random() * (ch2 - ch1 + 1));
        }
        return ret;
    }

    public Dynamic(GetHeader header, String msg, OutputStream out, InputStream in, String save) {
        this.report = header;
        this.msg = msg;
        this.out = out;
        this.in = in;
        this.save = save;
    }

    public boolean contorl() {
        switch (report.getfunction()) {
            case "GET":
                return getDynamic(report.geturl());
            case "POST":
                return postDynamic(report.geturl());
            //return tmpsave();
            default:
                try {
                    out.close();
                } catch (IOException ex) {
                    return false;
                }
                return true;
        }
    }

    //cgi_bin_demo说明这是一个post demo，如果有兴趣你可以直接扩充，然后在postDynamic添加函数。
    public boolean cgi_bin_demo(PostMes postbh[]) {//临时文件是没有清除的哦，还没有写表单的处理，如果要处理表单，读取后应该删除内容。
        //System.out.print("testdemo");
        boolean flag = false;
        String filename = null;
        for (int i = 0; i < 10; i++) {
            filename = postbh[i].value.get("filename");
            // postbh[i].print();
            if (filename != null) {
                flag = true;
                try {
                    /*File file = new File(save + new String(filename.substring(1, filename.length() - 1).getBytes("ISO-8859-1"), "UTF-8"));
                    out = new FileOutputStream(file);
                    fs.Savefile(postbh[i].getplain(), out);*/
                    File file = new File(save + '/' + postbh[i].getfile(save));//非文件应该读取临时文件内容，还未封装
                    file.renameTo(new File(save + new String(filename.substring(1, filename.length() - 1).getBytes("ISO-8859-1"), "UTF-8")));
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        if (flag) {
            return getDynamic("/cgi-bin/get_demo");
        } else {
            return this.notfound.getfile404(out, null);
        }
    }

    //这个用来生成查看已经提交作业的人的信息
    public boolean cgi_bin_get_demo() {
        try {
            String first = "<!DOCTYPE html>\n"
                    + "<html lang=\"en\">\n"
                    + "<head>\n"
                    + "    <meta charset=\"UTF-8\">\n"
                    + "    <title>上传成功页面</title>\n"
                    + "</head>\n"
                    + "<style>\n"
                    + "    .content{\n"
                    + "        width: 100%;\n"
                    + "        text-align: center;\n"
                    + "        margin: 0 auto;\n"
                    + "        height:100%;\n"
                    + "    }\n"
                    + "    .input{\n"
                    + "        text-align: center;\n"
                    + "        margin: 0 auto;\n"
                    + "        margin-top: 200px;\n"
                    + "        border: 2px solid #3366dd;\n"
                    + "        width: 500px;\n"
                    + "        padding: 50px;\n"
                    + "        border-radius: 20px;\n"
                    + "    }\n"
                    + "    .bg{\n"
                    + "        position: absolute;\n"
                    + "        top: 0;\n"
                    + "        left:0;\n"
                    + "        background: url(\"../bg.jpg\") no-repeat;\n"
                    + "        width: 100%;\n"
                    + "        height:100%;\n"
                    + "        z-index: -100;\n"
                    + "        background-size: cover;\n"
                    + "    }\n"
                    + "    .uploader{\n"
                    + "        overflow-y: scroll;\n"
                    + "        overflow-x: hidden;\n"
                    + "    }\n"
                    + "    ::-webkit-scrollbar{width:0;}\n"
                    + "</style>\n"
                    + "<body>\n"
                    + "<div class=\"content\">\n"
                    + "    <div class=\"bg\"></div>\n"
                    + "    <div class=\"input\">\n"
                    + "        <h1 style=\"margin-bottom: 100px;\">作业上交系统</h1>\n"
                    + "        <h5>上传成功(可向下滑动)</h5>\n"
                    + "        <div class=\"uploader\" style=\"height: 100px;\">\n"
                    + "            <!--这里填上传信息-->\n";
            String next = "        </div>\n"
                    + "    </div>\n"
                    + "</div>\n"
                    + "</body>\n"
                    + "</html>";
            ArrayList<String> filelist = fs.getfilelist(save);
            String list = "";
            for (int i = 0; i < filelist.size(); i++) {
                list += "<p>" + filelist.get(i) + "</p>\n";
            }
            ResponedHeader headspond = new ResponedHeader();
            headspond.setstate(0);//设置状态码200
            headspond.setcontent_type("/x.html");//设置rfc文件类型
            //headspond.setetag();//设置etag
            byte p1[] = first.getBytes();
            byte p2[] = list.getBytes();
            byte p3[] = next.getBytes();
            headspond.setcontent(p1.length + p2.length + p3.length);//获取文件大小
            String rethead = headspond.HeadtoString();
            rethead += "\r\n";//报文结尾
            out.write(rethead.getBytes());
            out.write(p1);
            out.write(p2);
            out.write(p3);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public boolean postDynamic(String URL) {
        try {
            byte[] get = new byte[4096 * 1024];//4m字节空间
            int start = 0;
            int end = 0;
            int flag = 0;
            FileOutputStream fo = null;
            PostMes postbh[] = new PostMes[10];
            for (int i = 0; i < 10; i++) {
                postbh[i] = new PostMes();//限定接受最多10个参数
            }
            int blen = "--".length() + report.getboundary().length();//谷歌的post报文正文不同先找到正文部分是否有第一个边界
            while (true) {
                flag = msg.indexOf("--" + report.getboundary());
                if (flag != -1) {
                    msg = new String(msg.substring(flag + blen + 2));
                    break;
                } else {
                    int len = in.read(get);
                    msg += new String(get, 0, len, "ISO-8859-1");
                }
            }
            /*{
                end = msg.indexOf("--" + report.getboundary());
                if (end != -1 && msg.charAt(end + blen + 1) != '-') {
                }
            }*/
            int i = 0;
            boolean flags = true;
            while (i < 10) {
                if (flags)//头标记，如果是报文开始，生成一个临时文件存储
                {
                    start = end;
                    String filecache = this.getRandomCharacter('a', 'z');
                    File fd = new File(save + '/' + filecache);
                    fo = new FileOutputStream(fd);
                    int plain = postbh[0].PostRead(msg, filecache);
                    msg = msg.substring(plain);//正文存储到文件里
                    flags = false;
                }
                end = msg.indexOf("--" + report.getboundary());
                if (end != -1 && msg.charAt(end + blen + 1) != '-') {//非结尾
                    String save = msg.substring(start, end);
                    fs.Savefile(save, fo);
                    msg = msg.substring(end + blen);
                    if (msg.length() < 100) {
                        int len = in.read(get);
                        msg += new String(get, 0, len, "ISO-8859-1");
                    }
                    i++;
                    end = 0;
                    flags = true;
                    fo.close();
                    continue;//也许一个报文（我限定一次读取4m个字节）有n个boundary,处理完再接受数据
                } else if (msg.contains("--" + report.getboundary() + "--")) {
                    break;
                } else {
                    fs.Savefile(msg.substring(0, msg.length() - blen - 4), fo);//防止结尾有部分在报文里。
                    msg = msg.substring(msg.length() - blen - 4);
                    end = 0;
                }
                int len = in.read(get);
                msg += new String(get, 0, len, "ISO-8859-1");
            }
            fs.Savefile(msg.substring(0, msg.indexOf("--" + report.getboundary() + "--")), fo);
            fo.close();
            System.out.println("test");
            if (URL.equals("/cgi-bin/uploadfile"))//找到对应的cgi子过程
            {
                return cgi_bin_demo(postbh);
            }
        } catch (IOException ex) {
            return false;
        }
        //System.out.print("test");
        return notfound.getfile404(out, null);//没找到
    }

    public boolean getDynamic(String which) {
        if (which.equals("/cgi-bin/get_demo")) {
            this.cgi_bin_get_demo();
        }
        return notfound.getfile404(out, null);
        /*File file = new File(save + "/success.html");
        notfound.getfile200("./success.html", out, file, report.getetag());
        return false;*/
    }
}
