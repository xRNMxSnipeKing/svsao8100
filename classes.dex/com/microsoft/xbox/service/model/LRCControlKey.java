package com.microsoft.xbox.service.model;

public enum LRCControlKey {
    VK_PAD_DPAD_UP(22544),
    VK_PAD_DPAD_DOWN(22545),
    VK_PAD_DPAD_LEFT(22546),
    VK_PAD_DPAD_RIGHT(22547),
    VK_PAD_START(22548),
    VK_PAD_BACK(22549),
    VK_PAD_A(22528),
    VK_PAD_B(22529),
    VK_PAD_X(22530),
    VK_PAD_Y(22531),
    VK_INFO(22652),
    VK_TITLE(22663),
    VK_PAUSE(19),
    VK_PLAY(250),
    VK_STOP(22646),
    VK_RECORD(22647),
    VK_FASTFWD(22648),
    VK_REWIND(22649),
    VK_SKIP(22650),
    VK_REPLAY(22651),
    VK_VOLUME_MUTE(173),
    VK_VOLUME_DOWN(174),
    VK_VOLUME_UP(175),
    VK_DVDMENU(22640),
    VK_DISPLAY(22645),
    VK_0(48),
    VK_1(49),
    VK_2(50),
    VK_3(51),
    VK_4(52),
    VK_5(53),
    VK_6(54),
    VK_7(55),
    VK_8(56),
    VK_9(57),
    VK_STAR(22661),
    VK_POUND(22662),
    VK_ESCAPE(27),
    VK_RETURN(13),
    VK_MCE(22664),
    VK_PAD_XE(22536),
    EMPTY(0);
    
    private final int value;

    private LRCControlKey(int value) {
        this.value = value;
    }

    public int getKeyValue() {
        return this.value;
    }
}
