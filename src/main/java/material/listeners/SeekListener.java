package material.listeners;

import java.time.Duration;

public interface SeekListener {
    void seeked(Duration newCurrentTime);
}
