package lighthttpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author heweisheng software enginner
 */
public class Filedeal {

    public static int getFileSize1(File file) {
        if (file.exists() && file.isFile()) {
            return (int) file.length();
        }
        return 0;
    }
     public void Savefilebyte(byte[] msg,FileOutputStream fs)
    {
        //System.out.print(msg);
        try {                      
            fs.write(msg);
            System.out.print("1");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Filedeal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Filedeal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void Savefile(String msg,FileOutputStream fs)
    {
        //System.out.print(msg);
        try {           
            byte []stb=msg.getBytes("ISO-8859-1");
            fs.write(stb);
            System.out.print("1");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Filedeal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Filedeal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void Sendfile(OutputStream out, File file)//文件发送
    {
        try {
            FileInputStream fs = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            try {
                int all = fs.available();             //判断文件长度
                while ((all = all - fs.read(buffer)) > 0) {
                    out.write(buffer);          //多次发送
                    //System.out.printf(new String(buffer));
                }
                //System.out.printf(new String(buffer));
                out.write(buffer);          //最后一次发送
                fs.close();             //一定要记得关闭
            } catch (IOException ex) {
                Logger.getLogger(Lighthttpd.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lighthttpd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean judegfilexist(File file) //判断文件是否存在生成404或者返回文本/图片
    {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
    private final static String ENCODE = "UTF-8";           //URL解析，把UTF-8转回汉字

    public String getURLDecoderString(String str) {

        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Lighthttpd.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
