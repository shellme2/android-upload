package com.eebbk.bfc.uploadsdk.upload;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtrasConverter {
    private static final char TAG_B = '{';
    private static final char TAG_E = '}';
    private static final char TAG_S = ':';

    private static final String CODE_FORMAT = "UTF-8";
    private static final Pattern ENCODE_PATTERN = Pattern.compile("[^(){}:]+");

    private ExtrasConverter(){}

    public static String encode(HashMap<String, String> hashMap){
        if(hashMap == null){
            throw new IllegalArgumentException(
                    "The hashMap is null can't be encode!");
        }
        StringBuilder ret = new StringBuilder();
        @SuppressWarnings("rawtypes")
        Iterator iter = hashMap.entrySet().iterator();
        ret.append('(');
        while(iter.hasNext()){
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            ret.append(encodeFormat(key, val));
        }
        ret.append(')');
        return ret.toString();
    }

    public static String encodeFormat(String key, String val){
        StringBuilder ret = new StringBuilder();
        try {
            key = encodeCheck(key);
            val = encodeCheck(val);
            ret.append(TAG_B);
            ret.append(URLEncoder.encode(key, CODE_FORMAT));
            ret.append(TAG_S);
            ret.append(URLEncoder.encode(val, CODE_FORMAT));
            ret.append(TAG_E);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ret.toString();
    }

    private static String encodeCheck(String str){
        String ret;
        if(str == null){
            ret = "#null#";
        }else if(str.equals("")){
            ret = "#space#";
        }else{
            ret = str;
        }

        return ret;
    }

    private static String decodeCheck(String str){
        String ret;
        switch (str) {
            case "#null#":
                ret = null;
                break;
            case "#space#":
                ret = "";
                break;
            default:
                ret = str;
                break;
        }

        return ret;
    }

    public static HashMap<String, String> decode(String strEncode)
            throws UnsupportedEncodingException{
        if(strEncode == null){
            throw new IllegalArgumentException(
                    "The String is null can't be decode!");
        }
        HashMap<String, String> map = new HashMap<>();
        try{
            String key = null;
            String value;
            Matcher m = ENCODE_PATTERN.matcher(strEncode);
            while(m.find()){
                if(key == null){
                    key = URLDecoder.decode(m.group(), CODE_FORMAT);
                }else{
                    value = URLDecoder.decode(m.group(), CODE_FORMAT);
                    key = decodeCheck(key);
                    value = decodeCheck(value);
                    map.put(key, value);
                    key = null;
                }
            }
        }catch(IllegalStateException ex){
            ex.printStackTrace();
        }
        return map;
    }

}
