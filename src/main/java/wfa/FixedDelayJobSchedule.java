/*
Copyright (c) 2014, Brent Worden
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of Brent Worden nor the names of the
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package wfa;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A fixed delay {@link JobSchedule} implementation. The next scheduled execution time is a fixed amount of time after
 * the last completion time for the job.
 */
public class FixedDelayJobSchedule implements JobSchedule {

    /** the fixed delay in milliseconds. */
    private final long delayInMillis;

    /**
     * Construct a new schedule with the given fixed delay measured in milliseconds.
     * 
     * @param delayInMillis
     *            fixed delay measured in milliseconds.
     */
    public FixedDelayJobSchedule(long delayInMillis) {
        super();
        this.delayInMillis = delayInMillis;
    }

    /**
     * Construct a new schedule with the given fixed delay measured in the given units of time.
     * 
     * @param delay
     *            the fixed delay measured in {@code units}.
     * @param units
     *            the units of time for the delay.
     */
    public FixedDelayJobSchedule(long delay, TimeUnit units) {
        this(TimeUnit.MILLISECONDS.convert(delay, units));
    }

    /**
     * Compute the next scheduled execution time for a job based on the last completion time. The next scheduled
     * execution time is {@code lastCompletionTimeMillis.getTime()} + {@link #delayInMillis} returned as a date.
     * 
     * @param lastScheduledExecutionTime
     *            ignored by this implementation
     * @param lastActualExecutionTime
     *            ignored by this implementation
     * @param lastCompletionTime
     *            the last time the job execution actually completed.
     * @return the next scheduled execution time.
     */
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

    /**
     * Compute the next scheduled execution time. {@code lastCompletionTimeMillis} + {@link #delayInMillis} returned as
     * a date.
     * 
     * @param lastCompletionTimeMillis
     *            the last time the job execution actually completed.
     * @return the next scheduled execution time.
     */
    private Date nextScheduledExecutionTime(long lastCompletionTimeMillis) {
        return new Date(lastCompletionTimeMillis + delayInMillis);
    }
}
