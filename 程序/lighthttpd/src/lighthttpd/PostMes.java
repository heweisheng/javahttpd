/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lighthttpd;

import java.util.HashMap;
import java.util.Map;

/**
 * @author heweisheng software enginner
 */
public class PostMes {

    private String contentd=null;//Content-Disposition;
    private String conttype=null;//Content-Type;
    public HashMap<String, String> value = new HashMap();//key-value，懒，如果考虑效率可以不使用。
    private String plain;//废弃String存放，改用文件
    private String file;
    public void setfile(String file)
    {
        this.file=file;
    }
    public String getfile(String file)
    {
        return this.file;
    }
    public String getconttype()
    {
        return this.conttype;
    }
    public String getplain()
    {
        return this.plain;
    }
    public void print()//测试用，目前只知道能兼容火狐，ie，谷歌
    {
        for(Map.Entry<String, String> entry: value.entrySet())
        {
         System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        }
    }
    public void Postkv(String mes) {//跟报文头一样的处理。
        int start = 0;
        int end = 0;
        while (mes.charAt(end) != ';') {
            end++;//跳过form-data; 
        }
        start = end = end + 2;
        String key = "";
        String value = "";
        while (true) {
            while(mes.charAt(end)!='=')end++;
            key=mes.substring(start, end);
            start=end=end+1;
            while(mes.charAt(end)!=';'&&mes.charAt(end)!='\r')end++;//可能不止一个键值对
            value=mes.substring(start, end);
            if(mes.charAt(end)=='\r')
                break;
            start=end=end+2;
            this.value.put(key, value);
        }
        this.value.put(key, value);//存储到键值对
    }

    public int PostRead(String mes,String filename) {
        String key = "";
        String value = "";
        String plain = "";
        int start = 0;
        int end = 0;
        while (true) {
            while (mes.charAt(end) != ':') {
                end++;
            }
            key = mes.substring(start, end);
            start = end = end + 2;
            while (mes.charAt(end) != '\r') {
                end++;
            }
            value = mes.substring(start, end);
            if (key.equals("Content-Disposition")) {
                this.contentd = value+'\r';
                this.Postkv(this.contentd);
            } else if (key.equals("Content-Type")) {
                this.conttype = value;
            }
            if (mes.charAt(end + 2) == '\r') {
                break;
            }         
        }
        this.file=filename;
        return end+4;//正文部分起始位置\r\n\r\n的问题。
        //this.plain=mes.substring(end+4);
    }

}
