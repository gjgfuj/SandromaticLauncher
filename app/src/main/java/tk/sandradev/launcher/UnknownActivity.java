package tk.sandradev.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;


public class UnknownActivity extends Activity
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
            service.loadApps();
            Toast.makeText(UnknownActivity.this, R.string.invalid_app, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(UnknownActivity.this, AppListActivity.class);
            startActivity(i);
        }
    };
    @Override
    protected void onCreate(Bundle state)
    {
        super.onCreate(state);
        bindService(new Intent(this, AppsService.class), connection, Context.BIND_AUTO_CREATE);
    }
}
