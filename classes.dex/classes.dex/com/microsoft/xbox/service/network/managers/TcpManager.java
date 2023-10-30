package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public class TcpManager {
    private static final int InvalidTcpSocketIndex = 0;
    private static final int MaxTcpSockets = 10;
    private static int nextFreeTcpSocketIndex = 0;
    private static TcpSocket[] tcpSocketList = new TcpSocket[10];

    public static void sendTcpMessage(String host, int port, byte[] message, int ptrDelegateReceived, int ptrDelegateClosed) {
        XLELog.Diagnostic("TcpManager", "JNI calling Java for TCP message send to " + host);
        XLEThreadPool.networkOperationsThreadPool.run(new TcpSocket(host, port, message, Boolean.valueOf(false), ptrDelegateReceived, ptrDelegateClosed));
    }

    public static int createPersistentTcpTransport(String host, int port, int ptrDelegateReceived, int ptrDelegateClosed) {
        XLELog.Diagnostic("TcpManager", "JNI calling Java for TCP create to " + host);
        synchronized (tcpSocketList) {
            int i;
            int firstSocketIndex = nextFreeTcpSocketIndex;
            while (tcpSocketList[nextFreeTcpSocketIndex] != null) {
                i = nextFreeTcpSocketIndex + 1;
                nextFreeTcpSocketIndex = i;
                nextFreeTcpSocketIndex = i % 10;
                if (nextFreeTcpSocketIndex == firstSocketIndex) {
                    return 0;
                }
            }
            int foundIndex = nextFreeTcpSocketIndex;
            tcpSocketList[foundIndex] = new TcpSocket(host, port, Boolean.valueOf(true), ptrDelegateReceived, ptrDelegateClosed);
            XLEThreadPool.networkOperationsThreadPool.run(tcpSocketList[foundIndex]);
            i = nextFreeTcpSocketIndex + 1;
            nextFreeTcpSocketIndex = i;
            nextFreeTcpSocketIndex = i % 10;
            i = foundIndex + 1;
            return i;
        }
    }

    public static void sendPersistentTcpMessage(byte[] message, int oneBasedSocketIndex) {
        XLELog.Diagnostic("TcpManager", "JNI calling Java for TCP send, length " + message.length);
        synchronized (tcpSocketList) {
            if (oneBasedSocketIndex > 0 && oneBasedSocketIndex <= 10) {
                if (tcpSocketList[oneBasedSocketIndex - 1] != null) {
                    tcpSocketList[oneBasedSocketIndex - 1].send(message);
                }
            }
            XLELog.Error("TcpManager", "Attempting to call send on a non-existing socket");
        }
    }

    public static void closePersistentTcpTransport(int oneBasedSocketIndex) {
        XLELog.Diagnostic("TcpManager", "JNI calling Java for TCP close");
        synchronized (tcpSocketList) {
            if (oneBasedSocketIndex > 0 && oneBasedSocketIndex <= 10) {
                if (tcpSocketList[oneBasedSocketIndex - 1] != null) {
                    tcpSocketList[oneBasedSocketIndex - 1].shutdown();
                    tcpSocketList[oneBasedSocketIndex - 1] = null;
                }
            }
            XLELog.Error("TcpManager", "Attempting to call close on a non-existing socket");
        }
    }
}
