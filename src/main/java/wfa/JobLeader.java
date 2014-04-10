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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLeader extends LeaderSelectorListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(JobLeader.class);

    private CuratorFramework client;

    private JobExecutor executor;

    private Job job;

    private LeaderSelector leader;

    private JobSchedule schedule;

    private String statusPath;

    public void destroyLeader() {
        executor.cancel(job);
        leader.close();
    }

    public void initializeLeader() {
        statusPath = "/job/" + job.getUniqueName() + "/status";

        // select leader
        leader = new LeaderSelector(client, "/job/" + job.getUniqueName() + "/leader", this);
        leader.autoRequeue();
        leader.start();
    }

    private Date readLongAsDate(DataInputStream dos) {
        try {
            final long value = dos.readLong();
            return new Date(value);
        } catch (final IOException ex) {
            LOG.warn("could not read status fully", ex);
        }
        return new Date(0L);
    }

    private JobStatus readStatus() throws Exception {

        final JobStatus status = new JobStatus();

        final Stat stat = client.checkExists().forPath(statusPath);
        if (stat == null) {
            client.create().forPath(statusPath);
            status.setLastActualCompletionTime(null);
            status.setLastActualExecutionTime(null);
            status.setLastScheduledExecutionTime(null);
        } else {
            final byte[] data = client.getData().forPath(statusPath);

            final ByteArrayInputStream baos = new ByteArrayInputStream(data);
            try (DataInputStream dos = new DataInputStream(baos)) {
                status.setLastActualCompletionTime(readLongAsDate(dos));
                status.setLastActualExecutionTime(readLongAsDate(dos));
                status.setLastScheduledExecutionTime(readLongAsDate(dos));
            }
        }
        return status;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public void setExecutor(JobExecutor executor) {
        this.executor = executor;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setSchedule(JobSchedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        LOG.info("elected leader of job {}.", job.getUniqueName());

        // read lastest status
        final JobStatus status = readStatus();

        // determine next execution time
        final Date nextScheduledExecutionTime = schedule.nextScheduledExecutionTime(
            status.getLastScheduledExecutionTime(), status.getLastActualExecutionTime(),
            status.getLastActualCompletionTime());

        // create job execution context
        final JobContext ctx = new JobContext(job, nextScheduledExecutionTime);

        executor.execute(ctx);

        // persist status
        status.setLastActualCompletionTime(ctx.getActualCompletionTime());
        status.setLastActualExecutionTime(ctx.getActualExecutionTime());
        status.setLastScheduledExecutionTime(nextScheduledExecutionTime);
        writeStatus(status);

        LOG.info("relinquished leadership of job {}.", job.getUniqueName());
    }

    private void writeDateAsLong(Date date, DataOutputStream dos) throws IOException {
        if (date == null) {
            dos.writeLong(0L);
        } else {
            dos.writeLong(date.getTime());
        }
    }

    private void writeStatus(JobStatus status) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            writeDateAsLong(status.getLastActualCompletionTime(), dos);
            writeDateAsLong(status.getLastActualExecutionTime(), dos);
            writeDateAsLong(status.getLastScheduledExecutionTime(), dos);
        }

        final byte[] data = baos.toByteArray();
        client.setData().forPath(statusPath, data);
    }
}
