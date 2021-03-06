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

/**
 * A {@link Job} implementation that simply delegates execution to a {@link Runnable} instance by calling its
 * {@link Runnable#run()} method.
 */
public class RunnableJob extends AbstractJob {

    /**
     * Construct a new job using the given runnable target.
     * 
     * @param runnable
     *            the runnable target for this job.
     */
    public RunnableJob(Runnable runnable) {
        super();
        this.runnable = runnable;
    }

    /** the runnable target for this job. */
    private final Runnable runnable;

    /**
     * Execute this job using the given context. This method should only be called by the currently elected job leader.
     * This implementation merely calls the {@link Runnable#run} method on {@link RunnableJob#runnable}
     */
    @Override
    public void execute(JobContext ctx) {
        runnable.run();
    }
}
