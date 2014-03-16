package wfa;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FixedDelayJobSchedule implements JobSchedule {

    private final long delayInMillis;

    public FixedDelayJobSchedule(long delayInMillis) {
        super();
        this.delayInMillis = delayInMillis;
    }

    public FixedDelayJobSchedule(long delay, TimeUnit units) {
        this(TimeUnit.MILLISECONDS.convert(delay, units));
    }

    @Override
    public Date nextScheduledExecutionTime(Date lastScheduledExecutionTime, Date lastActualExecutionTime,
        Date lastCompletionTime) {

        if (lastCompletionTime == null) {
            // use now as the last completion time
            return nextScheduledExecutionTime(System.currentTimeMillis());
        }

        // add the delay to the last completion time
        return nextScheduledExecutionTime(lastCompletionTime.getTime());
    }

    private Date nextScheduledExecutionTime(long lastCompletionTimeMillis) {
        return new Date(lastCompletionTimeMillis + delayInMillis);
    }
}
