package com.eebbk.bfc.uploadsdk.upload.net;



public interface NetworkType {
    int NETWORK_UNKNOWN = 0;
    int NETWORK_MOBILE = 1;// 1 << 0;
    int NETWORK_WIFI = 1 << 1;
    int NETWORK_BLUETOOTH = 1 << 2;
    int DEFAULT_NETWORK = NETWORK_WIFI | NETWORK_BLUETOOTH;
}
