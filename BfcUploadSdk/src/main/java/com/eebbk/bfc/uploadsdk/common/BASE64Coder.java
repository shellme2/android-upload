package com.eebbk.bfc.uploadsdk.common;


import com.org.apache.commons.codec.binary.Base64;

class BASE64Coder {

    private BASE64Coder(){
    }
      
    /** 
     * BASE64加密 
     *  
     * @param data 
     * @return 
     * @throws Exception
     */  
    public static String encode(byte[] data) throws Exception {
        return new String(Base64.encodeBase64(data), "utf-8");
    }

}
