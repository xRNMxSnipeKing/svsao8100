package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import java.util.ArrayList;

public class TcpSocket implements Runnable {
    private static final int TcpBufferSize = 4096;
    private static final int TcpSocketTimeout = 5000;
    private static final int TcpThreadInputPollMilliseconds = 33;
    private String host;
    private ArrayList<byte[]> messages;
    private int port;
    private int ptrDelegateClosed;
    private int ptrDelegateReceived;
    private Boolean shouldPersist;
    private Boolean shouldStop;
    private Object workQueueLock;

    public static native void onSendPersistentTcpMessageReceived(byte[] bArr, int i);

    public static native void onSendTcpMessageReceived(byte[] bArr, int i);

    public TcpSocket(String host, int port, Boolean shouldPersist, int ptrDelegateReceived, int ptrDelegateClosed) {
        this(host, port, null, shouldPersist, ptrDelegateReceived, ptrDelegateClosed);
    }

    public TcpSocket(String host, int port, byte[] message, Boolean shouldPersist, int ptrDelegateReceived, int ptrDelegateClosed) {
        this.workQueueLock = new Object();
        this.ptrDelegateReceived = 0;
        this.ptrDelegateClosed = 0;
        this.messages = new ArrayList();
        this.shouldPersist = Boolean.valueOf(false);
        this.shouldStop = Boolean.valueOf(false);
        XLELog.Diagnostic("TcpSocket", "JNI calling Java for TCP connect to " + host);
        this.host = host;
        this.port = port;
        this.shouldPersist = shouldPersist;
        this.ptrDelegateReceived = ptrDelegateReceived;
        this.ptrDelegateClosed = ptrDelegateClosed;
        if (message != null) {
            send(message);
        }
    }

    public void send(byte[] message) {
        XLELog.Diagnostic("TcpSocket", "JNI calling Java for TCP send, length " + message.length);
        synchronized (this.workQueueLock) {
            this.messages.add(message);
            this.workQueueLock.notifyAll();
        }
    }

    public void shutdown() {
        synchronized (this.workQueueLock) {
            this.shouldStop = Boolean.valueOf(true);
            this.workQueueLock.notifyAll();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r16 = this;
        r10 = new java.net.Socket;	 Catch:{ Exception -> 0x00f8 }
        r10.<init>();	 Catch:{ Exception -> 0x00f8 }
        r1 = new java.net.InetSocketAddress;	 Catch:{ Exception -> 0x00f8 }
        r0 = r16;
        r12 = r0.host;	 Catch:{ Exception -> 0x00f8 }
        r0 = r16;
        r13 = r0.port;	 Catch:{ Exception -> 0x00f8 }
        r1.<init>(r12, r13);	 Catch:{ Exception -> 0x00f8 }
        r12 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r10.connect(r1, r12);	 Catch:{ Exception -> 0x00f8 }
        r12 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r10.setSoTimeout(r12);	 Catch:{ Exception -> 0x00f8 }
        r12 = 1;
        r10.setTcpNoDelay(r12);	 Catch:{ Exception -> 0x00f8 }
        r8 = r10.getOutputStream();	 Catch:{ Exception -> 0x00f8 }
        r6 = r10.getInputStream();	 Catch:{ Exception -> 0x00f8 }
        r12 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r3 = new byte[r12];	 Catch:{ Exception -> 0x00f8 }
    L_0x002c:
        r7 = 0;
        r2 = new java.io.ByteArrayOutputStream;	 Catch:{ Exception -> 0x00f8 }
        r2.<init>();	 Catch:{ Exception -> 0x00f8 }
        r0 = r16;
        r13 = r0.workQueueLock;	 Catch:{ Exception -> 0x00f8 }
        monitor-enter(r13);	 Catch:{ Exception -> 0x00f8 }
        r0 = r16;
        r12 = r0.shouldStop;	 Catch:{ all -> 0x011f }
        r12 = r12.booleanValue();	 Catch:{ all -> 0x011f }
        if (r12 == 0) goto L_0x0046;
    L_0x0041:
        monitor-exit(r13);	 Catch:{ all -> 0x011f }
    L_0x0042:
        r10.close();	 Catch:{ Exception -> 0x00f8 }
    L_0x0045:
        return;
    L_0x0046:
        r0 = r16;
        r12 = r0.messages;	 Catch:{ all -> 0x011f }
        r12 = r12.size();	 Catch:{ all -> 0x011f }
        if (r12 <= 0) goto L_0x005d;
    L_0x0050:
        r0 = r16;
        r12 = r0.messages;	 Catch:{ all -> 0x011f }
        r14 = 0;
        r12 = r12.remove(r14);	 Catch:{ all -> 0x011f }
        r0 = r12;
        r0 = (byte[]) r0;	 Catch:{ all -> 0x011f }
        r7 = r0;
    L_0x005d:
        monitor-exit(r13);	 Catch:{ all -> 0x011f }
        if (r7 == 0) goto L_0x007c;
    L_0x0060:
        r12 = "TcpSocket";
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00f8 }
        r13.<init>();	 Catch:{ Exception -> 0x00f8 }
        r14 = "JNI calling Java for TCP send, length ";
        r13 = r13.append(r14);	 Catch:{ Exception -> 0x00f8 }
        r14 = r7.length;	 Catch:{ Exception -> 0x00f8 }
        r13 = r13.append(r14);	 Catch:{ Exception -> 0x00f8 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x00f8 }
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r12, r13);	 Catch:{ Exception -> 0x00f8 }
        r8.write(r7);	 Catch:{ Exception -> 0x00f8 }
    L_0x007c:
        r11 = 0;
        r12 = 1;
        r5 = java.lang.Boolean.valueOf(r12);	 Catch:{ Exception -> 0x00f8 }
    L_0x0082:
        r12 = r5.booleanValue();	 Catch:{ Exception -> 0x00f8 }
        if (r12 == 0) goto L_0x0092;
    L_0x0088:
        r0 = r16;
        r12 = r0.shouldPersist;	 Catch:{ Exception -> 0x00f8 }
        r12 = r12.booleanValue();	 Catch:{ Exception -> 0x00f8 }
        if (r12 == 0) goto L_0x0098;
    L_0x0092:
        r12 = r6.available();	 Catch:{ Exception -> 0x00f8 }
        if (r12 <= 0) goto L_0x00a3;
    L_0x0098:
        r12 = 0;
        r5 = java.lang.Boolean.valueOf(r12);	 Catch:{ Exception -> 0x00f8 }
        r9 = r6.read(r3);	 Catch:{ Exception -> 0x00f8 }
        if (r9 >= 0) goto L_0x0122;
    L_0x00a3:
        r0 = r16;
        r12 = r0.shouldPersist;	 Catch:{ Exception -> 0x00f8 }
        r12 = r12.booleanValue();	 Catch:{ Exception -> 0x00f8 }
        if (r12 == 0) goto L_0x00af;
    L_0x00ad:
        if (r11 <= 0) goto L_0x00cc;
    L_0x00af:
        r0 = r16;
        r12 = r0.shouldStop;	 Catch:{ Exception -> 0x00f8 }
        r12 = r12.booleanValue();	 Catch:{ Exception -> 0x00f8 }
        if (r12 != 0) goto L_0x00cc;
    L_0x00b9:
        r12 = r2.toByteArray();	 Catch:{ Exception -> 0x00f8 }
        r0 = r16;
        r0.sendResponse(r12);	 Catch:{ Exception -> 0x00f8 }
        r0 = r16;
        r12 = r0.shouldPersist;	 Catch:{ Exception -> 0x00f8 }
        r12 = r12.booleanValue();	 Catch:{ Exception -> 0x00f8 }
        if (r12 == 0) goto L_0x0042;
    L_0x00cc:
        r0 = r16;
        r13 = r0.workQueueLock;	 Catch:{ Exception -> 0x00f8 }
        monitor-enter(r13);	 Catch:{ Exception -> 0x00f8 }
    L_0x00d1:
        r0 = r16;
        r12 = r0.shouldStop;	 Catch:{ all -> 0x00f5 }
        r12 = r12.booleanValue();	 Catch:{ all -> 0x00f5 }
        if (r12 != 0) goto L_0x0129;
    L_0x00db:
        r0 = r16;
        r12 = r0.messages;	 Catch:{ all -> 0x00f5 }
        r12 = r12.isEmpty();	 Catch:{ all -> 0x00f5 }
        if (r12 == 0) goto L_0x0129;
    L_0x00e5:
        r12 = r6.available();	 Catch:{ all -> 0x00f5 }
        if (r12 != 0) goto L_0x0129;
    L_0x00eb:
        r0 = r16;
        r12 = r0.workQueueLock;	 Catch:{ all -> 0x00f5 }
        r14 = 33;
        r12.wait(r14);	 Catch:{ all -> 0x00f5 }
        goto L_0x00d1;
    L_0x00f5:
        r12 = move-exception;
        monitor-exit(r13);	 Catch:{ all -> 0x00f5 }
        throw r12;	 Catch:{ Exception -> 0x00f8 }
    L_0x00f8:
        r4 = move-exception;
        r12 = "TcpSocket";
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r14 = "failed to send and read message to socket with ";
        r13 = r13.append(r14);
        r14 = r4.toString();
        r13 = r13.append(r14);
        r13 = r13.toString();
        com.microsoft.xbox.toolkit.XLELog.Error(r12, r13);
        r12 = 0;
        r12 = new byte[r12];
        r0 = r16;
        r0.sendResponse(r12);
        goto L_0x0045;
    L_0x011f:
        r12 = move-exception;
        monitor-exit(r13);	 Catch:{ all -> 0x011f }
        throw r12;	 Catch:{ Exception -> 0x00f8 }
    L_0x0122:
        r11 = r11 + r9;
        r12 = 0;
        r2.write(r3, r12, r9);	 Catch:{ Exception -> 0x00f8 }
        goto L_0x0082;
    L_0x0129:
        monitor-exit(r13);	 Catch:{ all -> 0x00f5 }
        goto L_0x002c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.service.network.managers.TcpSocket.run():void");
    }

    private void sendResponse(byte[] response) {
        final byte[] finalResponse = response;
        XLELog.Diagnostic("TcpSocket", "TCP message received, calling back to JNI, length " + response.length + (this.shouldPersist.booleanValue() ? " (persistent)" : " (non-persistent)"));
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                XLELog.Warning("TcpSocket", "running task on " + Thread.currentThread().getId());
                if (TcpSocket.this.shouldPersist.booleanValue()) {
                    TcpSocket.onSendPersistentTcpMessageReceived(finalResponse, TcpSocket.this.ptrDelegateReceived);
                } else {
                    TcpSocket.onSendTcpMessageReceived(finalResponse, TcpSocket.this.ptrDelegateReceived);
                }
            }
        });
    }
}
