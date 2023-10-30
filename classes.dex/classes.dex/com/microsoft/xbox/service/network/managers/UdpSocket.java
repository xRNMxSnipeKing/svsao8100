package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UdpSocket implements Runnable {
    private static final int PACKET_SIZE = 5000;
    private InetAddress group;
    private int messageReceivedDelegate;
    private MulticastSocket socket;
    private boolean stopped = true;

    public static native void onMulticastMessageReceived(String str, int i, byte[] bArr, int i2, int i3);

    public UdpSocket(String groupAddr, int port, int delegate) {
        XLELog.Diagnostic("UdpSocket", "Creating udpsocket for " + groupAddr);
        this.messageReceivedDelegate = delegate;
        try {
            this.group = InetAddress.getByName(groupAddr);
            this.socket = new MulticastSocket(port);
            this.socket.joinGroup(this.group);
            this.stopped = false;
            XLELog.Diagnostic("UdpSocket", "socket successfully created");
        } catch (Exception e) {
            XLELog.Error("UdpSocket", "failed to join group with exception " + e.toString());
            this.group = null;
            this.socket = null;
            this.stopped = true;
        }
    }

    public void stop() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.stopped = true;
        cleanup();
    }

    public void run() {
        XLELog.Warning("UdpSocket", "the thread started ...");
        while (!this.stopped) {
            DatagramPacket packet = new DatagramPacket(new byte[5000], 5000);
            try {
                this.socket.receive(packet);
                final byte[] receivedData = packet.getData();
                final int length = packet.getLength();
                final String hostAddr = packet.getAddress().getHostAddress();
                final int port = packet.getPort();
                final int delegatePtr = this.messageReceivedDelegate;
                XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                    public void run() {
                        XLELog.Diagnostic("UdpSocket", " calling onMulticastMessageReceived");
                        if (!UdpSocket.this.stopped) {
                            UdpSocket.onMulticastMessageReceived(hostAddr, port, receivedData, length, delegatePtr);
                        }
                    }
                });
            } catch (Exception e) {
                XLELog.Error("UdpSocket", "failed to receive packet with exception " + e.toString());
            }
        }
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                UdpSocket.this.cleanup();
                MulticastManager.UnregisterMulticast();
            }
        });
        XLELog.Warning("UdpSocket", "the thread is exiting...");
    }

    private void cleanup() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!(this.socket == null || this.group == null)) {
            try {
                XLELog.Warning("UdpSocket", "leave the multicast group");
                this.socket.leaveGroup(this.group);
            } catch (IOException e) {
                XLELog.Warning("UdpSocket", "failed to leave the group " + e.toString());
            }
            this.socket.close();
        }
        this.socket = null;
        this.group = null;
    }
}
