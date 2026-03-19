package com.mediatek.plugin.preload;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import com.mediatek.plugin.element.PluginDescriptor;
import com.mediatek.plugin.res.ApkResource;
import com.mediatek.plugin.res.IResource;
import com.mediatek.plugin.utils.ArrayUtils;
import com.mediatek.plugin.utils.Log;
import com.mediatek.plugin.utils.TraceHelper;
import com.mediatek.plugin.zip.ApkFile;
import com.mediatek.plugin.zip.JarFile;
import com.mediatek.plugin.zip.ZipCenter;
import com.mediatek.plugin.zip.ZipFile;
import java.io.IOException;
import java.io.InputStream;

public class Preloader {
    private static final String NAME_XSD = "plugin.xsd";
    private static final String TAG = "PluginManager/Preloader";
    private static Preloader sPreloader;
    private Signature[] mHostSignature;
    private boolean mHasInitEnvironment = false;
    private int mHostVersion = -1;

    public static Preloader getInstance() {
        if (sPreloader == null) {
            sPreloader = new Preloader();
        }
        return sPreloader;
    }

    public PluginDescriptor preloadPlugin(Context context, String str, String str2, boolean z, boolean z2) {
        TraceHelper.beginSection(">>>>Preloader-preloadPlugin");
        initPreloadEnviorment(context, z);
        ZipFile zipFileCreateZipFile = ZipCenter.createZipFile(str);
        if (zipFileCreateZipFile == null) {
            Log.d(TAG, "<preloadPlugin> Cannot find the ZipFile to process, return null");
            TraceHelper.endSection();
            return null;
        }
        if (z) {
            Signature[] signature = zipFileCreateZipFile.getSignature();
            if (this.mHostSignature == null || signature == null || !ArrayUtils.areExactMatch(this.mHostSignature, signature)) {
                Log.d(TAG, "<preloadPlugin> Signature not match, return null");
                zipFileCreateZipFile.recycle();
                TraceHelper.endSection();
                return null;
            }
        }
        if (z2 && !zipFileCreateZipFile.validateXML(getXsdInputStream(context))) {
            Log.e(TAG, "<preloadPlugin> Schema validate fail, return null");
            zipFileCreateZipFile.recycle();
            TraceHelper.endSection();
            return null;
        }
        IResource resource = zipFileCreateZipFile.getResource(context);
        PluginDescriptor pluginDescriptor = (PluginDescriptor) new XMLParser(zipFileCreateZipFile.getXmlInputStream(), resource).parserXML();
        if (pluginDescriptor == null) {
            Log.e(TAG, "<preloadPlugin> parserXML return null, return null");
            zipFileCreateZipFile.recycle();
            TraceHelper.endSection();
            return null;
        }
        if (!isMatchHostVersion(pluginDescriptor)) {
            Log.e(TAG, "<preloadPlugin> Version is not match with host, return null");
            zipFileCreateZipFile.recycle();
            TraceHelper.endSection();
            return null;
        }
        pluginDescriptor.setArchivePath(str);
        if (zipFileCreateZipFile instanceof ApkFile) {
            ApkResource apkResource = (ApkResource) resource;
            pluginDescriptor.setAssetManager(apkResource.getAssetManager());
            pluginDescriptor.setResource(apkResource.getResources());
            pluginDescriptor.setPackageInfo(((ApkFile) zipFileCreateZipFile).getPackageInfo(context));
        }
        zipFileCreateZipFile.copySoLib(context, str2);
        zipFileCreateZipFile.recycle();
        TraceHelper.endSection();
        return pluginDescriptor;
    }

    private synchronized void initPreloadEnviorment(Context context, boolean z) {
        if (!this.mHasInitEnvironment) {
            TraceHelper.beginSection(">>>>initPreloadEnviorment");
            ZipCenter.registerZipFile(ApkFile.getSuffix(), ApkFile.class);
            ZipCenter.registerZipFile(JarFile.getSuffix(), JarFile.class);
            if (z) {
                initHostSignature(context);
            }
            initHostVersion(context);
            this.mHasInitEnvironment = true;
            TraceHelper.endSection();
        }
    }

    private boolean isMatchHostVersion(PluginDescriptor pluginDescriptor) {
        if (this.mHostVersion == -1 || this.mHostVersion > pluginDescriptor.requireMaxHostVersion || this.mHostVersion < pluginDescriptor.requireMinHostVersion) {
            Log.d(TAG, "<isMatchHostVersion> version unvalidate! hostVersion " + this.mHostVersion + ", plugin requireMaxHostVersion " + pluginDescriptor.requireMaxHostVersion + ", plugin requireMinHostVersion " + pluginDescriptor.requireMinHostVersion);
            return false;
        }
        return true;
    }

    private void initHostVersion(Context context) {
        try {
            try {
                TraceHelper.beginSection(">>>>Preloader-initHostVersion");
                this.mHostVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                Log.d(TAG, "<initHostVersion> mHostVersion = " + this.mHostVersion);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Invalid package name for context", e);
            }
        } finally {
            TraceHelper.endSection();
        }
    }

    private void initHostSignature(Context context) {
        TraceHelper.beginSection(">>>>Preloader-initHostSignature");
        this.mHostSignature = SignatureParser.parseSignature(context);
        Log.d(TAG, "<initHostSignature> mHostSignature = " + this.mHostSignature);
        TraceHelper.endSection();
    }

    private InputStream getXsdInputStream(Context context) {
        try {
            TraceHelper.beginSection(">>>>Preloader-getXsdInputStream");
            return context.getAssets().open(NAME_XSD);
        } catch (IOException e) {
            Log.e(TAG, "<initXSDInputStream> IOException", e);
            return null;
        } finally {
            TraceHelper.endSection();
        }
    }
}
