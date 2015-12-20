package tk.sandradev.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HomeActivity extends Activity
{

    AppsService service = null;
    ServiceConnection connection = new ServiceConnection()
    {
        public void onServiceDisconnected(ComponentName componentName)
        {
            service = null;
        }

        public void onServiceConnected(ComponentName componentName, IBinder binder)
        {
            service = ((AppsService.ServiceBinder) binder).getService();
            loadView();
        }
    };

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RelativeLayout l = (RelativeLayout) this.findViewById(R.id.tllayout);
        l.setOnTouchListener(new HomeGestureListener());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        startService(new Intent(this, AppsService.class));
        bindService(new Intent(this, AppsService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService(connection);
    }

    private void loadView()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        GridLayout layout = (GridLayout) findViewById(R.id.grid);
        layout.removeAllViewsInLayout();
        layout.setColumnCount(point.x / service.DPPixels);
        for (int i = 0;i<service.homeApps.size();i++)
        {
            final AppInfo app = service.homeApps.get(i);
            ImageButton view = new ImageButton(this);
            view.setImageDrawable(app.icon);
            view.setAdjustViewBounds(true);
            view.setMaxWidth(service.DPPixels);
            view.setMaxHeight(service.DPPixels);
            view.setBackgroundResource(android.R.color.transparent);
            final int index = i;
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent();
                    i.setComponent(new ComponentName((String) app.packageName, (String) app.name));
                    startActivity(i);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    if (app.label=="Unknown App")
                    {
                        service.homeApps.remove(index);
                        service.saveApps();
                        Intent i = new Intent(HomeActivity.this, AppListActivity.class);
                        startActivity(i);
                        return true;
                    }
                    Intent i = new Intent(HomeActivity.this, AppInfoActivity.class);
                    i.putExtra("name", (String) app.name);
                    i.putExtra("packageName", (String) app.packageName);
                    startActivity(i);
                    return true;
                }
            });
            layout.addView(view);
        }
    }

    public void showApps(View v)
    {
        Intent i = new Intent(this, AppListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.abc_slide_in_top, 0);
    }

    public void showWidgets(View v)
    {
        //Intent i = new Intent(this, WidgetActivity.class);
        //startActivity(i);
    }

    public void preferences(View v)
    {
        //Intent i = new Intent(this, PreferencesActivity.class);
        Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
    }

    class HomeGestureListener extends GestureListener
    {
        public void onLeftSwipe()
        {
            Intent i = new Intent();
            i.setComponent(new ComponentName((String) service.leftApp.packageName, (String) service.leftApp.name));
            startActivity(i);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

        public void onRightSwipe()
        {
            Intent i = new Intent();
            i.setComponent(new ComponentName((String) service.rightApp.packageName, (String) service.rightApp.name));
            startActivity(i);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }

        public void onDownSwipe()
        {
            showApps(null);
        }

        public void onUpSwipe()
        {
            showWidgets(null);
        }
    }
}
