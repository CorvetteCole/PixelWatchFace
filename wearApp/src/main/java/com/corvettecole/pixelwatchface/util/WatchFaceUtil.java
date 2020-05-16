package com.corvettecole.pixelwatchface.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.Calendar;

public class WatchFaceUtil {

  public static final long ONE_MIN = 60000;

  // TODO can this be replaced by this: Bitmap hourHand = BitmapFactory.decodeResource(context.getResources(), R.drawable.hour_hand);? Which is faster
  public static Bitmap drawableToBitmap(Drawable drawable) {
    Bitmap mBitmap = null;

    if (drawable instanceof BitmapDrawable) {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
      if (bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }

    if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
      mBitmap = Bitmap.createBitmap(1, 1,
          Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
    } else {
      mBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
          Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(mBitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return mBitmap;
  }

  public static int getHour(Calendar mCalendar, Boolean mUse24HourTime) {
    if (mUse24HourTime) {
      return mCalendar.get(Calendar.HOUR_OF_DAY);
    } else {
      int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
      if (hour == 0) {
        return 12;
      } else if (hour > 12) {
        return hour - 12;
      } else {
        return hour;
      }
    }
  }

  public static double convertToCelsius(double fahrenheit) {
    return (fahrenheit - 32) / 1.8;
  }

  public static <T> void observeOnce(final LiveData<T> liveData, final Observer<T> observer) {
    liveData.observeForever(new Observer<T>() {
      @Override
      public void onChanged(T t) {
        liveData.removeObserver(this);
        observer.onChanged(t);
      }
    });
  }

}
