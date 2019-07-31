package app.helpers;

import java.util.*;

/**
 * This class implements the timeout handler
 * for set a timeout given a completion runnable
 * to call on timeout triggered. Can reset
 * trigger.
 */
public class Timeout extends TimerTask {
    private static final long REFRESH_RATE = 1000;

    public final Timer timer;
    public final long duration;
    public final Runnable completion;

    private long time;

    public Timeout(Runnable completion, long duration) {
        this.timer = new Timer();
        this.duration = duration;
        this.completion = completion;
        this.reset();
    }

    public Timeout start() {
        this.timer.scheduleAtFixedRate(this, 0, REFRESH_RATE);
        return this;
    }

    public void stop() {
        timer.cancel();
        reset();
    }

    public void reset() {
        this.time = 0;
    }

    @Override
    public void run() {
        if (duration <= time) {
            stop();
            completion.run();
        } else {
            time += REFRESH_RATE;
        }
    }
}
