package nro.models.server;

import java.util.concurrent.atomic.AtomicBoolean;
import nro.models.services.Service;
import nro.models.utils.Logger;

/**
 *
 * @author By Mr Blue
 *
 */
public class Maintenance extends Thread {

    private static Maintenance instance;
    private int timeInSeconds;
    public static volatile boolean isRunning = false;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    private Maintenance() {
    }

    public static Maintenance gI() {
        if (instance == null) {
            instance = new Maintenance();
        }
        return instance;
    }

    public void startCountdown() {
        startSeconds(60);
    }

    public void startSeconds(int seconds) {
        if (isStarted.compareAndSet(false, true)) {
            isRunning = true;
            this.timeInSeconds = seconds;
            Thread.ofVirtual().name("Maintenance-Thread").start(this);
        }
    }

    public void startImmediately() {
        if (isStarted.compareAndSet(false, true)) {
            isRunning = true;
            Logger.log(Logger.YELLOW, "BẮT ĐẦU BẢO TRÌ NGAY\n");
            ServerManager.gI().close();
        }
    }

    @Override
    public void run() {
        Logger.log(Logger.YELLOW, "Bắt đầu đếm ngược " + timeInSeconds + "s bảo trì");

        while (timeInSeconds > 0) {
            try {
                sendRemainingTime();
                Thread.sleep(1000);
                timeInSeconds--;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Logger.log(Logger.YELLOW, "BẢO TRÌ BẮT ĐẦU\n");
        ServerManager.gI().close();
    }

    private void sendRemainingTime() {
        String msg = "Hệ thống sẽ bảo trì sau " + timeInSeconds + " giây nữa. Hãy thoát game để tránh mất dữ liệu.";
        Service.gI().sendThongBaoAllPlayer(msg);
        Logger.log(Logger.YELLOW, msg);
    }
}
