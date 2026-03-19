package com.android.wallpaper.livepicker;

import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class LiveWallpaperChange extends LiveWallpaperPreview {
    @Override
    protected void init() {
        Parcelable parcelableExtra = getIntent().getParcelableExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT");
        if (parcelableExtra == null || !(parcelableExtra instanceof ComponentName)) {
            Log.w("CHANGE_LIVE_WALLPAPER", "No LIVE_WALLPAPER_COMPONENT extra supplied");
            finish();
            return;
        }
        ComponentName componentName = (ComponentName) parcelableExtra;
        Intent intent = new Intent("android.service.wallpaper.WallpaperService");
        intent.setPackage(componentName.getPackageName());
        List<ResolveInfo> listQueryIntentServices = getPackageManager().queryIntentServices(intent, 128);
        if (listQueryIntentServices != null) {
            for (int i = 0; i < listQueryIntentServices.size(); i++) {
                ResolveInfo resolveInfo = listQueryIntentServices.get(i);
                if (resolveInfo.serviceInfo.name.equals(componentName.getClassName())) {
                    try {
                        initUI(new WallpaperInfo(this, resolveInfo));
                        return;
                    } catch (IOException | XmlPullParserException e) {
                        Log.w("CHANGE_LIVE_WALLPAPER", "Bad wallpaper " + resolveInfo.serviceInfo, e);
                        finish();
                        return;
                    }
                }
            }
        }
        Log.w("CHANGE_LIVE_WALLPAPER", "Not a live wallpaper: " + componentName);
        finish();
    }
}
