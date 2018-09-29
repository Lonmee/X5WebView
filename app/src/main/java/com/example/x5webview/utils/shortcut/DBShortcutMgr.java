package com.example.x5webview.utils.shortcut;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

public class DBShortcutMgr {
    private static DBShortcutMgr instance;

    public static DBShortcutMgr getInstance() {
        return DBShortcutMgr.instance == null ? (DBShortcutMgr.instance = new DBShortcutMgr()) : DBShortcutMgr.instance;
    }

    public void createShortcut(Context context, String scName, int sId, String iconURL) {
        iconURL = "http://dev.local.yunyun-inc.com:8000/story/1377/icon.jpg?t=1534760617";
        Bitmap iconBDlocal = getBitMapFormUrl(iconURL);

        if (iconBDlocal != null) {//36 48 72 96
            // 获得图片的宽高
            int width = iconBDlocal.getWidth();
            int height = iconBDlocal.getHeight();
            // 设置想要的大小
            int newWidth = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 96 : 72;
            int newHeight = newWidth;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            float scale = scaleHeight > scaleWidth ? scaleHeight : scaleWidth;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            // 得到新的图片
            iconBDlocal = Bitmap.createBitmap(iconBDlocal, 0, 0, width, height, matrix, true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // >= api26
                ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

                List<ShortcutInfo> pinnedShortcuts = shortcutManager.getPinnedShortcuts();
                for (int i = 0; i < pinnedShortcuts.size(); i++) {
                    ShortcutInfo info = pinnedShortcuts.get(i);
                    if (info.getId().equals(scName)) {
                        Toast.makeText(context, "shortcut named '" + scName + "' exist", Toast.LENGTH_SHORT).show();
                        if (info.isEnabled()) {//当pinned shortcut为disable时，重复创建会导致应用奔溃
                            return;
                        } else {
                            return;
                        }
                    }
                }

                if (shortcutManager.isRequestPinShortcutSupported()) {
                    Intent receiverIntent = new Intent(Intent.ACTION_MAIN);
                    receiverIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    receiverIntent.setComponent(new ComponentName(context.getPackageName(), context.getClass().getName()));
                    receiverIntent.setComponent(new ComponentName("com.example.x5webview", "com.example.x5webview.utils.shortcut.X5AgentActivity"));
                    receiverIntent.putExtra("name", scName);
                    receiverIntent.putExtra("sId", sId);

                    ShortcutInfo info = new ShortcutInfo.Builder(context, scName)
                            .setIcon(Icon.createWithBitmap(iconBDlocal))
                            .setShortLabel(scName)
                            .setIntent(receiverIntent)
                            .build();

                    Intent intent = new Intent(context, context.getClass());
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    shortcutManager.requestPinShortcut(info, PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).getIntentSender());

                }
            } else { // < api26
                Intent receiverIntent = new Intent(Intent.ACTION_MAIN);
                receiverIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                receiverIntent.setComponent(new ComponentName("com.example.x5webview", "com.example.x5webview.utils.shortcut.X5AgentActivity"));
                receiverIntent.putExtra("name", scName);
                receiverIntent.putExtra("sId", sId);

                Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                intent.putExtra("duplicate", false);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, scName);

                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconBDlocal);
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, receiverIntent);
                context.sendBroadcast(intent);
            }
        } else {
            Toast.makeText(context, "创建失败 - no Bmp data", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public boolean isExist(Context context, String scName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // >= api26
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            List<ShortcutInfo> pinnedShortcuts = shortcutManager.getPinnedShortcuts();
            for (int i = 0; i < pinnedShortcuts.size(); i++) {
                ShortcutInfo info = pinnedShortcuts.get(i);
                if (info.getId().equals(scName)) {
                    if (info.isEnabled()) {//当pinned shortcut为disable时，重复创建会导致应用奔溃
                        return true;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        } else {
            String readSettingsPermission = "com.android.launcher.permission.READ_SETTINGS";
            String authority = getAuthorityFromPermission2(context, readSettingsPermission);
            String url = "content://" + authority + "/favorites?notify=true";
            final Uri CONTENT_URI = Uri.parse(url);
            Cursor c = context.getContentResolver().query(CONTENT_URI, null, " title= ? ", new String[]{scName}, null);
            if (c != null && c.moveToNext()) {
                return true;
            }
            return false;
        }
    }

    @SuppressLint("NewApi")
    private static String getAuthorityFromPermission2(Context context, String permission) {
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission))
                            return provider.authority;
                        if (permission.equals(provider.writePermission))
                            return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    private boolean getAuthorityFromPermission(Context context) {
        // 先得到默认的Launcher
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager mPackageManager = context.getPackageManager();
        ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
        if (resolveInfo == null) {
            return false;
        }
        @SuppressLint("WrongConstant") List<ProviderInfo> info = mPackageManager.queryContentProviders(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.applicationInfo.uid, PackageManager.GET_PROVIDERS);
        if (info != null) {
            for (int j = 0; j < info.size(); j++) {
                ProviderInfo provider = info.get(j);
                if (provider.readPermission == null) {
                    continue;
                }
                if (Pattern.matches(".*launcher.*READ_SETTINGS", provider.readPermission)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    private Bitmap getBitMapFormUrl(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}