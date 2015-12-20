package tk.sandradev.launcher;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by sandra on 3/5/15.
 */
public class WidgetActivity extends Activity
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
        }
    };
    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        LinearLayout l = (LinearLayout) this.findViewById(R.id.tllayout);
        l.setOnTouchListener(new WidgetGestureListener());
        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

    }

    void selectWidget()
    {
        int appWidgetId = appWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);

    }

    void addEmptyData(Intent pickIntent)
    {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == R.id.REQUEST_PICK_APPWIDGET)
            {
                configureWidget(data);
            } else if (requestCode == R.id.REQUEST_CREATE_APPWIDGET)
            {
                createWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null)
        {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1)
            {
                appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void configureWidget(Intent data)
    {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null)
        {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
        } else
        {
            createWidget(data);
        }
    }

    public void createWidget(Intent data)
    {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView = appWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        LinearLayout layout = (LinearLayout)findViewById(R.id.tllayout);
        layout.addView(hostView);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        startService(new Intent(this, AppsService.class));
        bindService(new Intent(this, AppsService.class), connection, Context.BIND_AUTO_CREATE);
        appWidgetHost.startListening();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService(connection);
        appWidgetHost.stopListening();
    }

    class WidgetGestureListener extends GestureListener
    {
        public void onLeftSwipe()
        {
            selectWidget();
        }

        public void onUpSwipe()
        {

        }

        public void onDownSwipe()
        {
            Intent i = new Intent(WidgetActivity.this, HomeActivity.class);
            startActivity(i);
        }

        public void onRightSwipe()
        {

        }
    }
}
