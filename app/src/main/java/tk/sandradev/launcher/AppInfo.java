package tk.sandradev.launcher;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import org.json.JSONException;
import org.json.JSONObject;


public class AppInfo
{
    CharSequence label;
    CharSequence name;
    CharSequence packageName;
    Drawable icon;

    public static AppInfo fromJSON(JSONObject json, AppsService service)
    {
        try
        {
            String name = json.getString("name");
            String packageName = json.getString("packageName");
            for (AppInfo info : service.apps)
            {
                if (name.equals(info.name) && packageName.equals(info.packageName))
                {
                    return info;
                }
            }
        }
        catch (JSONException e)
        {
            try
            {
                String name = json.getString("name");
                for (AppInfo info : service.apps)
                {
                    if (name.equals(info.name))
                    {
                        return info;
                    }
                }
            }
            catch (Exception ex)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        AppInfo blankInfo = new AppInfo();
        Intent intent = new Intent(service, UnknownActivity.class);
        blankInfo.packageName = intent.getComponent().getPackageName();
        blankInfo.name = intent.getComponent().getClassName();
        blankInfo.label = "Unknown App";
        blankInfo.icon = service.getResources().getDrawable(R.drawable.ic_unknown);
        return blankInfo;
    }
    public boolean equals(Object object) {
        return object instanceof AppInfo && (((AppInfo) object).name.equals(name) && ((AppInfo) object).packageName.equals(packageName));
    }
    public JSONObject toJSON(AppsService service)
    {
        try
        {
            JSONObject object = new JSONObject();
            object.put("name", name);
            object.put("packageName", packageName);
            return object;
        } catch (JSONException e)
        {
            return null;
        }
    }
}
