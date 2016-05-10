package tk.sandradev.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AppListActivity extends Activity
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this, AppsService.class), connection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_app_list);
        LinearLayout l = (LinearLayout) this.findViewById(R.id.tllayout);
        l.setOnTouchListener(new AppListGestureListener());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(connection);
    }

    private void loadView()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        GridLayout layout = (GridLayout) findViewById(R.id.grid);
        layout.setColumnCount(point.x / service.DPPixels);
        for (final AppInfo app : service.apps)
        {
            ImageButton view = new ImageButton(this);
            view.setImageDrawable(app.icon);
            view.setAdjustViewBounds(true);
            view.setMaxWidth(service.DPPixels);
            view.setMaxHeight(service.DPPixels);
            view.setBackgroundResource(android.R.color.transparent);
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
                    Intent i = new Intent(AppListActivity.this, AppInfoActivity.class);
                    i.putExtra("name", (String) app.name);
                    i.putExtra("packageName", (String) app.packageName);
                    startActivity(i);
                    return true;
                }
            });
            layout.addView(view);
        }
    }

    class AppListGestureListener extends GestureListener
    {
        public void onLeftSwipe()
        {

        }

        public void onDownSwipe()
        {

        }

        public void onUpSwipe()
        {
            Intent i = new Intent(AppListActivity.this, HomeActivity.class);
            startActivity(i);
        }

        public void onRightSwipe()
        {

        }
    }

}
