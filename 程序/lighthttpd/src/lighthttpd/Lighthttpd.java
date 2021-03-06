package lighthttpd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author heweisheng software enginner
 */
public class Lighthttpd implements Runnable {

    Filedeal fd = new Filedeal();
    Getstatic getstatic = new Getstatic();
    Socket cli = null;
    static String local = null;
    static String save = null;

    public Lighthttpd(Socket cli) {
        this.cli = cli;
    }

    public Lighthttpd() {
        this.cli = null;
    }

    @Override
    public void run() {
        try {
            if (cli != null) {
                deal(cli);
            } else {
                work();
            }
        } catch (IOException ex) {
            Logger.getLogger(Lighthttpd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deal(Socket cli) throws IOException {
        String msg = new String("");
        InputStream in = cli.getInputStream();
        OutputStream out = cli.getOutputStream();
        byte get[] = new byte[4096];
        while (true) {
            int len = in.read(get);
            msg = msg + new String(get, 0, len, "ISO-8859-1");//如果不实用ISO格式数据会失真，在文件传输是致命的,另外还有缓冲区覆盖问题，主要是byte不初始化
            if (msg.indexOf("\r\n\r\n") == 0) {//报文后面可能有post正文
                continue;
            } else {
                break;
            }
        }
        //建议一个一个解析，因为如果每个字段都去找报文效率太低，不如逐个解析速度快
        GetHeader report = new GetHeader();
        int end = msg.indexOf("\r\n\r\n") + 4;//正文部分为end后。
        report.Readheader(msg.substring(0, end));//报文头解析
        String method = report.getfunction();
        if (method.equals("GET") && !report.geturl().contains("/cgi-bin/")) {//post方法一定是动态的，get方法可以是静态也可以动态
            String URL = fd.getURLDecoderString(report.geturl());       //URL转换       
            File file = new File(local + URL);//查找静态文件夹的
            String etag = report.getetag();
            if (fd.judegfilexist(file)) {//存在文件，交给控制器产生页面
                if (!getstatic.contorl(URL, out, file, etag)) {
                    System.out.println("连接" + cli.getInetAddress() + ':' + cli.getLocalPort() + "意外断开");
                }
            } else {
                if (!getstatic.getfile404(out, etag)) {//不存在，返回404页面
                    System.out.println("连接" + cli.getInetAddress() + ':' + cli.getPort() + "意外断开");
                }
            }
        } else if (report.geturl().contains("/cgi-bin/")) {
            Dynamic dy = new Dynamic(report, msg.substring(end), out, in, save);//把正文部分交给动态处理机，同时把头部信息也传入
            dy.contorl();
        }

    }

    public boolean Init(String local, String save) {
        this.local = local + "\\";
        this.save = save + "\\";
        return !(this.local.equals("") || this.save.equals(""));
    }

    public void work() {
        try {
            ServerSocket ser = new ServerSocket(80);
            ExecutorService pool = Executors.newFixedThreadPool(8);
            while (true) {//循环等待新连接
                Socket cli = ser.accept();
                Lighthttpd request = new Lighthttpd(cli);
                pool.submit(request);//分配线程
            }
        } catch (IOException ex) {
            Logger.getLogger(Lighthttpd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
