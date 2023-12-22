package com.java110.hal.rtsp;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface IEvent {
    void connect(SelectionKey sk) throws IOException;

    void read(SelectionKey sk) throws IOException;

    void error(Exception e);
}
