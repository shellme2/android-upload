package com.eebbk.bfc.uploadsdk.da;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.eebbk.bfc.sdk.behavior.aidl.BfcBehaviorAidl;
import com.eebbk.bfc.sdk.behavior.aidl.listener.OnServiceConnectionListener;
import com.eebbk.bfc.uploadsdk.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UploadSdkDaService extends Service {
    public static final String TAG = "UploadSdkDaService";
    private BfcBehaviorAidl mBfcBehaviorAidl;
    private List<DaInfo> mCache = new ArrayList<>();
    private static final int BIND_STATE_IDLE = 0;
    private static final int BIND_STATE_CONNECTING = 1;
    private static final int BIND_STATE_CONNECTED = 2;
    private final AtomicInteger mBindState = new AtomicInteger(BIND_STATE_IDLE);
    private final AtomicLong mDaId = new AtomicLong();

    @Override
    public void onCreate() {
        super.onCreate();
        mBfcBehaviorAidl = new BfcBehaviorAidl.Builder()
                .setOnServiceConnectionListener(mListener)
                .setModuleName(Da.constant.MODULE_NAME)
                .build(null);
        mBfcBehaviorAidl.putAttr("packageName", "com.eebbk.bfc.uploadsdk");
        mBfcBehaviorAidl.putAttr("appVer", BuildConfig.VERSION_NAME);
        mBindState.set(BIND_STATE_CONNECTING);
        mBfcBehaviorAidl.bindService(getApplicationContext());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBfcBehaviorAidl.unbindService(getApplicationContext());
        mBindState.set(BIND_STATE_IDLE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        synchronized (TAG){
            String functionName = intent.getStringExtra(Da.constant.bundleKey.FUNCTION_NAME);
            String moduleDetail = intent.getStringExtra(Da.constant.bundleKey.MODULE_DETAIL);
            String trigValue = intent.getStringExtra(Da.constant.bundleKey.TRIG_VALUE);
            String extend = intent.getStringExtra(Da.constant.bundleKey.EXTEND);
            if(mBfcBehaviorAidl.isConnectionService()){
                mBfcBehaviorAidl.customEvent(null, functionName, moduleDetail, trigValue, extend,
                        String.valueOf(mDaId.getAndIncrement()), null, null, null, null, null, null, null);
            }else {
                mCache.add(new DaInfo()
                        .setFunctionName(functionName)
                        .setModuleDetail(moduleDetail)
                        .setTrigValue(trigValue)
                        .setExtend(extend));
                if(mBindState.get() == BIND_STATE_IDLE){
                    mBindState.set(BIND_STATE_CONNECTING);
                    mBfcBehaviorAidl.bindDefaultSystemService(getApplicationContext());
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private OnServiceConnectionListener mListener = new OnServiceConnectionListener() {
        @Override
        public void onConnected() {
            mBindState.set(BIND_STATE_CONNECTED);
            if(mCache.size() == 0){
                return;
            }
            for (DaInfo info : mCache) {
                mBfcBehaviorAidl.customEvent(null, info.getFunctionName(), info.getModuleDetail(), info.getTrigValue(), info.getExtend(),
                        String.valueOf(mDaId.getAndIncrement()), null, null, null, null, null, null, null);
            }
            mCache.clear();
        }

        @Override
        public void onDisconnected() {
            mBindState.set(BIND_STATE_IDLE);
        }
    };

}
