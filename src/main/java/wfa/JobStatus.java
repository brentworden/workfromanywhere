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
 * Job state that is maintained between job executions. Every time a job executes, the latest state is retrieved before
 * execution. Then, it is updated after execution. Finally it is stored for use on the next execution.
 */
public class JobStatus {

    /** the actual time the job completed its last execution. */
    private Date lastActualCompletionTime;

    /** the actual time the job started its last execution. */
    private Date lastActualExecutionTime;

    /** the time the job was scheduled to start its last execution. */
    private Date lastScheduledExecutionTime;

    public Date getLastActualCompletionTime() {
        return lastActualCompletionTime;
    }

    public Date getLastActualExecutionTime() {
        return lastActualExecutionTime;
    }

    public Date getLastScheduledExecutionTime() {
        return lastScheduledExecutionTime;
    }

    public void setLastActualCompletionTime(Date lastCompletionTime) {
        this.lastActualCompletionTime = lastCompletionTime;
    }

    public void setLastActualExecutionTime(Date lastExecutionTime) {
        this.lastActualExecutionTime = lastExecutionTime;
    }

    public void setLastScheduledExecutionTime(Date lastScheduledExecutionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
    }

}
