package wfa;

import java.util.Date;

/**
 * Policy used to determine future job execution times based on previous execution times
 */
public interface JobSchedule {

    /**
     * Compute the next scheduled execution time for a job based on three timings from the last execution.
     * 
     * @param lastScheduledExecutionTime
     *            the last time the job was <strong>scheduled</strong> to execute.
     * @param lastActualExecutionTime
     *            the last time the job was <strong>actually</strong> executed.
     * @param lastCompletionTime
     *            the last time the job execution actually completed.
     * @return the next scheduled execution time.
     */
    Date nextScheduledExecutionTime(Date lastScheduledExecutionTime, Date lastActualExecutionTime,
        Date lastCompletionTime);

}
