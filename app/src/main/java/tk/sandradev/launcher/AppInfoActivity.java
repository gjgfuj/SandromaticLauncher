package tk.sandradev.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.*;


public class AppInfoActivity extends Activity
{

    AppsService service = null;
    AppInfo appInfo = null;
    ServiceConnection connection = new ServiceConnection()
    {
        public void onServiceDisconnected(ComponentName componentName)
        {
            service = null;
        }

        public void onServiceConnected(ComponentName componentName, IBinder binder)
        {
            service = ((AppsService.ServiceBinder) binder).getService();
            onConnect();
        }
    };

    protected void onConnect()
    {
        String name = getIntent().getStringExtra("name");
        String packageName = getIntent().getStringExtra("packageName");
        for (AppInfo app : service.apps)
        {
            if (app.name.equals(name) && app.packageName.equals(packageName))
            {
                appInfo = app;
                break;
            }
        }
        if (appInfo == null)
        {
            Intent i = new Intent(this, AppListActivity.class);
            service.homeApps.remove(appInfo);
            service.saveApps();
            startActivity(i);
            return;
        }
        ((TextView) findViewById(R.id.label)).setText(appInfo.label);
        ((ImageView) findViewById(R.id.image)).setImageDrawable(appInfo.icon);
        if (service.homeApps.contains(appInfo))
        {
            findViewById(R.id.addToHome).setEnabled(false);
            findViewById(R.id.removeFromHome).setEnabled(true);
            int index = service.homeApps.indexOf(appInfo);
            if (index != 0) {
                findViewById(R.id.toStart).setEnabled(true);
                findViewById(R.id.left).setEnabled(true);
            }
            if (index != service.homeApps.size()-1) {
                findViewById(R.id.toEnd).setEnabled(true);
                findViewById(R.id.right).setEnabled(true);
            }
        }
    }

    public void launchApp(View v)
    {
        Intent i = service.manager.getLaunchIntentForPackage(appInfo.name.toString());
        startActivity(i);
    }

    public void addToHome(View v)
    {
        service.homeApps.add(appInfo);
        service.saveApps();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    public void removeFromHome(View v)
    {
        service.homeApps.remove(appInfo);
        service.saveApps();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
    public void toStart(View v)
    {
        service.homeApps.remove(appInfo);
        service.homeApps.add(0, appInfo);
        service.saveApps();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
    public void toEnd(View v)
    {
        service.homeApps.remove(appInfo);
        service.homeApps.add(appInfo);
        service.saveApps();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
    public void left(View v)
    {
        int index = service.homeApps.indexOf(appInfo);
        service.homeApps.remove(index);
        service.homeApps.add(index-1, appInfo);
        service.saveApps();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
    public void right(View v)
    {
        int index = service.homeApps.indexOf(appInfo);
        service.homeApps.remove(index);
        service.homeApps.add(index+1, appInfo);
        service.saveApps();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    public void openAppInfo(View v)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:"+ appInfo.packageName));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        bindService(new Intent(this, AppsService.class), connection, Context.BIND_AUTO_CREATE);
        RelativeLayout l = (RelativeLayout) this.findViewById(R.id.tllayout);
        l.setOnTouchListener(new AppInfoGestureListener());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(connection);
    }
    class AppInfoGestureListener extends GestureListener
    {
        public void onLeftSwipe()
        {
            service.leftApp = appInfo;
            service.saveApps();
            Intent i = new Intent(AppInfoActivity.this, HomeActivity.class);
            startActivity(i);
        }

        public void onDownSwipe()
        {

        }

        public void onUpSwipe()
        {

        }

        public void onRightSwipe()
        {
            service.rightApp = appInfo;
            service.saveApps();
            Intent i = new Intent(AppInfoActivity.this, HomeActivity.class);
            startActivity(i);
        }
    }

}
