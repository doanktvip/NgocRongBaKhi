package nro.models.network;

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import nro.models.interfaces.IMessageSendCollect;
import nro.models.interfaces.ISession;

public final class Sender implements Runnable {

    @NonNull
    private ISession session;
    @NonNull
    private BlockingDeque<Message> messages;
    private DataOutputStream dos;
    private IMessageSendCollect sendCollect;

    public Sender(@NonNull ISession session, @NonNull Socket socket) {
        if (session == null) {
            throw new NullPointerException("session is marked non-null but is null");
        }
        if (socket == null) {
            throw new NullPointerException("socket is marked non-null but is null");
        }
        try {
            this.session = session;
            this.messages = new LinkedBlockingDeque<>(2000);
            this.setSocket(socket);
        } catch (Exception exception) {
        }
    }

    public Sender setSocket(@NonNull Socket socket) {
        if (socket == null) {
            throw new NullPointerException("socket is marked non-null but is null");
        }
        try {
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException iOException) {
        }
        return this;
    }

    @Override
    public void run() {
        try {
            while (this.session != null && this.session.isConnected() && !Thread.currentThread().isInterrupted()) {
                Message message = this.messages.take();
                this.doSendMessage(message);
                message.cleanup();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
        }
    }

    public synchronized void doSendMessage(Message message) throws Exception {
        this.sendCollect.doSendMessage(this.session, this.dos, message);
    }

    public void sendMessage(Message msg) {
        try {
            if (this.session != null && this.session.isConnected() && this.messages != null && msg != null) {
                this.messages.offer(msg);
            }
        } catch (Exception exception) {
        }
    }

    public void setSend(IMessageSendCollect sendCollect) {
        this.sendCollect = sendCollect;
    }

    public int getNumMessages() {
        return this.messages.size();
    }

    public void close() {
        this.messages.clear();
        if (this.dos != null) {
            try {
                this.dos.close();
            } catch (IOException iOException) {
            }
        }
    }

    public void dispose() {
        this.session = null;
        this.messages = null;
        this.sendCollect = null;
        this.dos = null;
    }
}
