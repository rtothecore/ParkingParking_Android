package kr.co.ezinfotech.parkingparking;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import kr.co.ezinfotech.parkingparking.DATA.PZDataManager;
import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;

public class SplashPageProcessor extends Activity {
    Handler mHandler = null;

    public SplashPageProcessor() {
    }

    public void setHandler(Handler handlerVal) {
        mHandler = handlerVal;
    }

    public void runProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                /*
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message message = Message.obtain();
                message.arg1 = 777;
                mHandler.sendMessage(message);
                */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // DBManager.deleteTables();
                        PZDataManager pzdm = new PZDataManager(mHandler);
                        pzdm.initPzDB();
                    }
                });
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }
}
