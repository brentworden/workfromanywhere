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

/**
 * The state of a job during its current execution.
 */
public class JobContext {

    /** the actual time the job execution completed (set by the {@link JobExecutor}). */
    private Date actualCompletionTime;

    /** the actual time the job execution started (set by the {@link JobExecutor}). */
    private Date actualExecutionTime;

    /**
     * Construct a context using the given job and scheduled execution time.
     * @param job the job being executed
     * @param nextScheduledExecutionTime the time the job is scheduled to start execution.
     */
    public JobContext(Job job, Date nextScheduledExecutionTime) {
        super();
        this.job = job;
        this.nextScheduledExecutionTime = nextScheduledExecutionTime;
    }

    /** the job being executed. */
    private final Job job;

    /** the time the job is scheduled to start execution. */
    private final Date nextScheduledExecutionTime;

    public Date getActualCompletionTime() {
        return actualCompletionTime;
    }

    public Date getActualExecutionTime() {
        return actualExecutionTime;
    }

    public Job getJob() {
        return job;
    }

    public Date getNextScheduledExecutionTime() {
        return nextScheduledExecutionTime;
    }

    public void setActualCompletionTime(Date actualCompletionTime) {
        this.actualCompletionTime = actualCompletionTime;
    }

    public void setActualExecutionTime(Date actualExecutionTime) {
        this.actualExecutionTime = actualExecutionTime;
    }
}
