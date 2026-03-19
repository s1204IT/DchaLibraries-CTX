package com.mediatek.camera.common.mode;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import com.mediatek.camera.common.debug.LogHelper;
import com.mediatek.camera.common.debug.LogUtil;
import com.mediatek.camera.common.device.CameraDeviceManagerFactory;
import com.mediatek.camera.common.loader.DeviceDescription;
import com.mediatek.camera.common.loader.DeviceSpec;
import com.mediatek.camera.common.utils.CameraUtil;
import java.util.concurrent.ConcurrentHashMap;

public class CameraApiHelper {
    private static final LogUtil.Tag TAG = new LogUtil.Tag(CameraApiHelper.class.getSimpleName());
    private static DeviceSpec sDeviceSpec = new DeviceSpec();
    private static String mLogicalId = null;

    public static CameraDeviceManagerFactory.CameraApi getCameraApiType(String str) {
        return CameraDeviceManagerFactory.CameraApi.API2;
    }

    public static DeviceSpec getDeviceSpec(Context context) {
        createDeviceSpec(context);
        return sDeviceSpec;
    }

    public static String getLogicalCameraId() {
        return mLogicalId;
    }

    private static void createDeviceSpec(Context context) {
        CameraCharacteristics cameraCharacteristics;
        if (sDeviceSpec.getDefaultCameraApi() != null) {
            return;
        }
        CameraDeviceManagerFactory.CameraApi cameraApiType = getCameraApiType(null);
        int cameraNum = getCameraNum(context);
        ConcurrentHashMap<String, DeviceDescription> concurrentHashMap = new ConcurrentHashMap<>();
        if (cameraNum > 0) {
            for (int i = 0; i < cameraNum; i++) {
                DeviceDescription deviceDescription = new DeviceDescription(null);
                if (Build.VERSION.SDK_INT >= 21) {
                    try {
                        cameraCharacteristics = ((CameraManager) context.getSystemService("camera")).getCameraCharacteristics(String.valueOf(i));
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                        cameraCharacteristics = null;
                    }
                    deviceDescription.setCameraCharacteristics(cameraCharacteristics);
                    deviceDescription.storeCameraCharacKeys(cameraCharacteristics);
                }
                concurrentHashMap.put(String.valueOf(i), deviceDescription);
            }
            sDeviceSpec.setDefaultCameraApi(cameraApiType);
            sDeviceSpec.setDeviceDescriptions(concurrentHashMap);
        }
        LogHelper.i(TAG, "[createDeviceSpec] context: " + context + ", default api:" + cameraApiType + ", deviceDescriptionMap:" + concurrentHashMap + " cameraNum " + cameraNum);
    }

    public static int getCameraNum(Context context) {
        int length;
        CameraManager cameraManager;
        String[] cameraIdList;
        try {
            cameraManager = (CameraManager) context.getSystemService("camera");
            cameraIdList = cameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e = e;
            length = 0;
        }
        if (cameraIdList == null || cameraIdList.length == 0) {
            throw new RuntimeException("Camera num is 0, Sensor should double check");
        }
        length = cameraIdList.length;
        try {
            LogHelper.d(TAG, "<getCameraNum> idList length is " + cameraIdList.length);
            for (String str : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(str);
                if (CameraUtil.isSupportAvailableMode(cameraCharacteristics, "com.mediatek.multicamfeature.availableMultiCamFeatureMode", 1) && CameraUtil.getAvailableSessionKeys(cameraCharacteristics, "com.mediatek.multicamfeature.multiCamFeatureMode") != null) {
                    mLogicalId = str;
                    LogHelper.d(TAG, "<getCameraNum> mLogicalId is " + mLogicalId);
                }
            }
        } catch (CameraAccessException e2) {
            e = e2;
            e.printStackTrace();
        }
        return length;
    }
}
