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
    public HashMap<String, String> value = new HashMap();//key-value
    private String plain;
    public String getconttype()
    {
        return this.conttype;
    }
    public String getplain()
    {
        return this.plain;
    }
    public void print()
    {
        for(Map.Entry<String, String> entry: value.entrySet())
        {
         System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        }
    }
    public void Postkv(String mes) {
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
            while(mes.charAt(end)!=';'&&mes.charAt(end)!='\r')end++;
            value=mes.substring(start, end);
            if(mes.charAt(end)=='\r')
                break;
            start=end=end+2;
            this.value.put(key, value);
        }
        this.value.put(key, value);
    }

    public void PostRead(String mes) {
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
        this.plain=mes.substring(end+4);
    }

}
