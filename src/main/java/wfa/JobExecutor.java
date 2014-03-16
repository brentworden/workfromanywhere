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
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private final Map<String, ScheduledFuture<?>> jobFutures = new ConcurrentHashMap<String, ScheduledFuture<?>>();

    private ScheduledExecutorService scheduler;

    public void cancel(Job job) {
        cancel(job.getUniqueName());
    }

    private void cancel(String jobName) {
        final ScheduledFuture<?> future = jobFutures.remove(jobName);
        if (future != null) {
            cancel(jobName, future);
        }
    }

    private void cancel(String jobName, ScheduledFuture<?> future) {
        // attempt to cancel scheduled future
        if (future != null && !future.isDone() && !future.isCancelled()) {
            if (future.cancel(false)) {
                LOG.info("cancelled task for job {}.", jobName);
                future = null;
            } else {
                LOG.info("failed to cancel for job {}.", jobName);
            }
        }
    }

    public void destroyExecutor() {
        for (final Map.Entry<String, ScheduledFuture<?>> entry : jobFutures.entrySet()) {
            cancel(entry.getKey(), entry.getValue());
        }
        jobFutures.clear();
    }

    public void execute(final JobContext ctx) {
        final Job job = ctx.getJob();
        final String jobName = job.getUniqueName();
        final Date nextTime = ctx.getNextScheduledExecutionTime();

        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                ctx.setActualExecutionTime(new Date());
                try {
                    job.execute(ctx);
                } finally {
                    ctx.setActualCompletionTime(new Date());
                }
            }
        };

        final ScheduledFuture<?> future = scheduler.schedule(timer, nextTime.getTime() - System.currentTimeMillis(),
            TimeUnit.MILLISECONDS);
        jobFutures.put(jobName, future);
        LOG.info("scheduled job {} for execution at {}", jobName, nextTime);

        waitUntilComplete(jobName, future, ctx);

        jobFutures.remove(jobName);
    }

    private void populateMissingDates(JobContext ctx) {
        if (ctx.getActualExecutionTime() == null) {
            // it never executed
            ctx.setActualExecutionTime(new Date());
        }

        if (ctx.getActualCompletionTime() == null) {
            // it never completed
            ctx.setActualCompletionTime(new Date());
        }
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    private void waitUntilComplete(String jobName, ScheduledFuture<?> future, JobContext ctx) {
        final long delayInMillis = future.getDelay(TimeUnit.MILLISECONDS);
        if (delayInMillis > 0) {
            try {
                // delay until future is schedule to start
                Thread.sleep(delayInMillis);
            } catch (final InterruptedException ex) {
                LOG.info("interrupted waiting for job {} to start.", jobName);
            }
        }

        // wait until future has result
        do {
            try {
                future.get(1, TimeUnit.MINUTES);
            } catch (final TimeoutException ex) {
                LOG.info("job {} not done yet.", jobName);
                // continue
            } catch (final CancellationException ex) {
                LOG.info("job {} cancelled.", jobName);
                populateMissingDates(ctx);
                return;
            } catch (final ExecutionException ex) {
                LOG.info("job {} failed.", jobName);
                return;
            } catch (final InterruptedException ex) {
                LOG.info("job {} interupted.", jobName);
                return;
            }
        } while (!future.isDone() && !future.isCancelled());
    }

}
