package com.eebbk.bfc.uploadsdk.module.clouduploader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 文  件：TokenPojo.java
 * 公  司：步步高教育电子
 * 日  期：2016/5/24  9:39
 * 作  者：HeChangPeng
 */

public class TokenPojo {
    public static class DataPojo {
        public static class ClientToken {
            private HashMap<String, String> fileBody;
            private HashMap<String, String> stringBody;
            private HashMap<String, String> headMap;
            public HashMap<String, String> getFileBody() {
                return fileBody;
            }
            public void setFileBody(HashMap<String, String> fileBody) {
                this.fileBody = fileBody;
            }
            public HashMap<String, String> getStringBody() {
                return stringBody;
            }
            public void setStringBody(HashMap<String, String> stringBody) {
                this.stringBody = stringBody;
            }
            @Override
            public String toString() {
                return "ClientToken [fileBody=" + fileBody + ", stringBody="
                        + stringBody + ", headMap=" + headMap + "]";
            }

            public HashMap<String, String> getHeadMap() {
                return headMap;
            }

            public void setHeadMap(HashMap<String, String> headMap) {
                this.headMap = headMap;
            }
        }

        private String put_url;
        private ArrayList<ClientToken> clientToken;

        public String getPut_url() {
            return put_url;
        }

        public void setPut_url(String put_url) {
            this.put_url = put_url;
        }

        public ArrayList<ClientToken> getClientToken() {
            return clientToken;
        }

        public void setClientToken(ArrayList<ClientToken> clientToken) {
            this.clientToken = clientToken;
        }

        @Override
        public String toString() {
            return "DataPojo [put_url=" + put_url + ", clientToken="
                    + clientToken + "]";
        }

    }

    private DataPojo data;
    private String stateCode;
    private String stateInfo;

    public DataPojo getData() {
        return data;
    }

    public void setData(DataPojo data) {
        this.data = data;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    @Override
    public String toString() {
        return "TokenPojo [data=" + data + ", stateCode=" + stateCode
                + ", stateInfo=" + stateInfo + "]";
    }
}
