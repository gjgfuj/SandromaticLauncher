package tk.sandradev.launcher;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppsService extends Service
{
    public AppsService()
    {
    }
    public int DPPixels;
    @Override
    public void onCreate()
    {
        Resources r = getResources();
        DPPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
        super.onCreate();
        loadApps();
    }

    public void onDestroy()
    {
        super.onDestroy();
        saveApps();
    }

    public PackageManager manager;
    public List<AppInfo> apps;
    public List<AppInfo> homeApps;
    public AppInfo leftApp;
    public AppInfo rightApp;
    public int bindCounter = 0;

    public void loadApps()
    {
        manager = getPackageManager();
        apps = new ArrayList<>();
        homeApps = new ArrayList<>();

        Intent in = new Intent(Intent.ACTION_MAIN);
        in.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(in, 0);
        for (ResolveInfo ri : availableActivities)
        {
            AppInfo app = new AppInfo();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.name;
            app.packageName = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);
        }
        Intent leftIntent = new Intent(Settings.ACTION_SETTINGS);
        ComponentName leftName = leftIntent.resolveActivity(manager);
        leftApp = new AppInfo();
        leftApp.name = leftName.getClassName();
        leftApp.packageName = leftName.getPackageName();

        Intent rightIntent = new Intent("com.google.android.googlequicksearchbox.GOOGLE_SEARCH");
        ComponentName rightName = rightIntent.resolveActivity(manager);
        rightApp = new AppInfo();
        if (rightName == null) {

            rightIntent = new Intent(this, HomeActivity.class);
            rightName = rightIntent.resolveActivity(manager);
        }
            rightApp.name = rightName.getClassName();
            rightApp.packageName = rightName.getPackageName();

        try
        {
            FileInputStream inputStream = openFileInput("sandromaticlauncher.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String json = reader.readLine();
            if (json != null)
            {
                Log.i("JSON", json);
                JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
                JSONArray array = (JSONArray) object.get("homeApps");
                for (int i = 0; i < array.length(); i++)
                {
                    homeApps.add(AppInfo.fromJSON((JSONObject) array.get(i), this));
                }
                leftApp = AppInfo.fromJSON(object.getJSONObject("leftApp"), this);
                rightApp = AppInfo.fromJSON(object.getJSONObject("rightApp"), this);
            }
            reader.close();
            inputStream.close();
        } catch (IOException e)
        {
            Log.e("JSON", e.toString());
        } catch (JSONException e)
        {
            Log.e("JSON", e.toString());
        }

    }

    public void saveApps()
    {

        try
        {
            FileOutputStream outputStream = openFileOutput("sandromaticlauncher.json", Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            for (AppInfo info : homeApps)
            {
                array.put(info.toJSON(this));
            }
            object.put("homeApps", array);
            object.put("leftApp", leftApp.toJSON(this));
            object.put("rightApp", rightApp.toJSON(this));
            String json = object.toString();
            Log.i("JSON", json);
            writer.write(json);
            writer.close();
            outputStream.close();
        } catch (IOException e)
        {
            Log.e("JSON", e.toString());
        } catch (JSONException e)
        {
            Log.e("JSON", e.toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        if (bindCounter % 30 == 0)
            loadApps();
        return new ServiceBinder();
    }

    public class ServiceBinder extends Binder
    {
        public AppsService getService()
        {
            return AppsService.this;
        }
    }
}
