package lighthttpd;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//import sun.misc.BASE64Decoder;

/**
 * @author heweisheng software enginner
 */
public class Dynamic {

    //BASE64Decoder decoder = new BASE64Decoder();
    private GetHeader report = null;
    private String msg = null;
    private OutputStream out = null;
    private InputStream in = null;
    private Filedeal fs = new Filedeal();
    private Getstatic notfound = new Getstatic();
    private String save;

    public Dynamic(GetHeader header, String msg, OutputStream out, InputStream in, String save) {
        this.report = header;
        this.msg = msg;
        this.out = out;
        this.in = in;
        this.save = save;
    }

    public boolean contorl() {
        if (report.getfunction().equals("GET")) {
            return getDynamic();
        } else if (report.getfunction().equals("POST")) {
            return postDynamic(report.geturl());
            //return tmpsave();
        } else {
            try {
                out.close();
            } catch (IOException ex) {
                return false;
            }
            return true;
        }
    }

    public boolean cgi_bin_demo(PostMes postbh[]) {
        //System.out.print("testdemo");
        boolean flag = false;
        String filename = null;
        for (int i = 0; i < 10; i++) {
            filename = postbh[i].value.get("filename");          
           // postbh[i].print();
            if (filename != null) {
                flag = true;
                FileOutputStream out = null;
                try {
                    File file = new File(save + filename.substring(1, filename.length() - 1));
                    out = new FileOutputStream(file);
                    fs.Savefile(postbh[i].getplain(), out);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (flag) {
            return getDynamic();
        } else {
            return this.notfound.getfile404(out, null);
        }
    }

    public boolean postDynamic(String URL) {
        try {
            byte[] get = new byte[4096*1024];
            int start = 0;
            int end = 0;
            int flag = 0;
            PostMes postbh[] = new PostMes[10];
            for (int i = 0; i < 10; i++) {
                postbh[i] = new PostMes();//限定接受最多10个参数
            }           
            //System.out.println("--"+report.getboundary());
            //System.out.println(msg);
            int blen = "--".length() + report.getboundary().length();//谷歌的post报文正文不同先找到正文部分是否有第一个边界
            while (true) {
                flag = msg.indexOf("--" + report.getboundary());
                //System.out.println(flag);
                if (flag != -1) {
                    msg = new String(msg.substring(flag + blen+2));
                    //System.out.println(msg);
                    break;
                } else {
                    int len = in.read(get);
                    msg+=new String(get,0,len,"ISO-8859-1");
                }
            }
            String boundary = "";
            int i = 0;
            while (i < 10) {
                //System.out.println(msg);                                              
                start = end;
                end = msg.indexOf("--" + report.getboundary());
                if (end != -1 && msg.charAt(end + blen + 1) != '-') {
                    String save = msg.substring(start, end);
                    boundary += save;
                    postbh[i].PostRead(boundary);
                    msg = msg.substring(end + blen);
                    i++;
                    boundary = "";
                    end = 0;
                    continue;//也许一个报文（我限定一次读取4096个字节）有n个boundary,处理完再接受数据
                } else if (msg.contains("--" + report.getboundary() + "--")) {
                    break;
                } else {
                    boundary += msg;
                    //fd.Savefile(boundary,fs);
                    msg = "";
                    end = 0;
                }
                int len = in.read(get);
                msg += new String(get, 0, len, "ISO-8859-1");
                //System.out.print(new String(msg));
            }
            //System.out.println(msg);
            postbh[i].PostRead(boundary + msg.substring(0, msg.indexOf("--" + report.getboundary() + "--")));
            //postbh[i].print();
            System.out.println("test");
            //System.out.print(postbh[i].getplain());
            if (URL.equals("/cgi-bin/uploadfile"))//找到对应的cgi子过程
            {
                return cgi_bin_demo(postbh);
            }
            //System.out.println("test");
        } catch (IOException ex) {
            return false;
        }
        System.out.print("test");
        return notfound.getfile404(out, null);//没找到
    }

    public boolean getDynamic() {
        File file = new File(save + "/success.html");
        notfound.getfile200("./success.html", out, file, report.getetag());
        return false;
    }
}
