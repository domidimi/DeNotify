package de.domidimi.denotify;

import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.annotation.TargetApi;
import android.app.Notification;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedHelpers;


@TargetApi(19)
public class DeNotify implements IXposedHookLoadPackage {

   @Override
   public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
             
      if (!lpparam.packageName.equals("com.android.systemui"))
         return;
      
      XposedBridge.log("DeNotfy Inspect: " + lpparam.packageName);
      
      XposedHelpers.findAndHookMethod(
         "com.android.systemui.statusbar.CommandQueue",
         lpparam.classLoader,
         "addNotification",
         IBinder.class, StatusBarNotification.class,
         new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                  StatusBarNotification sBarNotification = ((StatusBarNotification)param.args[1]);
                  CharSequence notificationTitle = ((Notification)sBarNotification.getNotification()).
                     extras.getCharSequence(Notification.EXTRA_TITLE);

                  /*XposedBridge.log("DeNotify found notification " + notificationTitle);*/
                  if (notificationTitle.toString().contains("Privacy Guard")) {
                     param.setResult(null);
                     /*XposedBridge.log("DeNotify block " + notificationTitle);*/
                  }
            }
         }
      );
   }
}