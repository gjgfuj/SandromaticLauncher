package tk.sandradev.launcher;

import android.view.MotionEvent;
import android.view.View;

public abstract class GestureListener implements View.OnTouchListener
{
    float upX, upY, downX, downY;
    static final int MIN_DISTANCE = 100;

    public abstract void onLeftSwipe();

    public abstract void onRightSwipe();

    public abstract void onDownSwipe();

    public abstract void onUpSwipe();

    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > Math.abs(deltaY))
                {
                    if (Math.abs(deltaX) > MIN_DISTANCE)
                    {
                        // left or right
                        if (deltaX < 0)
                        {
                            this.onRightSwipe();
                            return true;
                        }
                        if (deltaX > 0)
                        {
                            this.onLeftSwipe();
                            return true;
                        }
                    } else
                    {
                        return false; // We don't consume the event
                    }
                }
                // swipe vertical?
                else
                {
                    if (Math.abs(deltaY) > MIN_DISTANCE)
                    {
                        // top or down
                        if (deltaY < 0)
                        {
                            this.onDownSwipe();
                            return true;
                        }
                        if (deltaY > 0)
                        {
                            this.onUpSwipe();
                            return true;
                        }
                    } else
                    {
                        return false; // We don't consume the event
                    }
                }

                return true;
            }
        }
        return false;
    }
}
