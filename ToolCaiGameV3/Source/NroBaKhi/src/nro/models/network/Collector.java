package nro.models.network;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import nro.models.interfaces.IMessageHandler;
import nro.models.interfaces.IMessageSendCollect;
import nro.models.interfaces.ISession;

public final class Collector
        implements Runnable {

    private ISession session;
    private DataInputStream dis;
    private IMessageSendCollect collect;
    private IMessageHandler messageHandler;

    public Collector(ISession session, Socket socket) {
        this.session = session;
        this.setSocket(socket);
    }

    public Collector setSocket(Socket socket) {
        try {
            this.dis = new DataInputStream(socket.getInputStream());
        } catch (IOException iOException) {
        }
        return this;
    }

    @Override
    public void run() {
        try {
            while (this.session != null && this.session.isConnected()) {
                Message msg = this.collect.readMessage(this.session, this.dis);
                if (msg.command == -27) {
                    this.session.sendKey();
                } else {
                    try {
                        this.messageHandler.onMessage(this.session, msg);
                    } catch (Throwable t) {
                        nro.models.utils.Logger.logException(Collector.class, (Exception) (t instanceof Exception ? t : new Exception(t)), "Lỗi onMessage cmd: " + msg.command);
                    }
                }
                msg.cleanup();
            }
        } catch (java.io.EOFException | java.net.SocketException | java.nio.channels.ClosedChannelException ignored) {
            // Ngắt kết nối bình thường từ client (tắt game, đóng kết nối)
        } catch (Exception exception) {
            nro.models.utils.Logger.logException(Collector.class, exception, "Lỗi Collector IP: " + (this.session != null ? this.session.getIP() : "unknown"));
        }
        try {
            Network.gI().getAcceptHandler().sessionDisconnect(this.session);
        } catch (Exception exception) {
        }
        if (this.session != null) {
            this.session.disconnect();
        }
    }

    public void setCollect(IMessageSendCollect collect) {
        this.collect = collect;
    }

    public void setMessageHandler(IMessageHandler handler) {
        this.messageHandler = handler;
    }

    public void close() {
        if (this.dis != null) {
            try {
                this.dis.close();
            } catch (IOException iOException) {
            }
        }
    }

    public void dispose() {
        this.session = null;
        this.dis = null;
        this.collect = null;
    }
}
