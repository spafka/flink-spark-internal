////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package org.apache.flink.api.java;
//
//import org.apache.flink.annotation.Public;
//import org.apache.flink.annotation.PublicEvolving;
//import org.apache.flink.api.common.InvalidProgramException;
//import org.apache.flink.api.common.JobExecutionResult;
//import org.apache.flink.api.common.JobID;
//import org.apache.flink.api.common.Plan;
//import org.apache.flink.api.common.PlanExecutor;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.configuration.RestOptions;
//
//@Public
//public class LocalEnvironment extends ExecutionEnvironment {
//    private final Configuration configuration;
//    private PlanExecutor executor;
//    private LocalEnvironment.ExecutorReaper executorReaper;
//
//    public LocalEnvironment() {
//        this(new Configuration());
//    }
//
//    public LocalEnvironment(Configuration config) {
//        if (!ExecutionEnvironment.areExplicitEnvironmentsAllowed()) {
//            throw new InvalidProgramException("The LocalEnvironment cannot be instantiated when running in a pre-defined context (such as Command Line Client, Scala Shell, or TestEnvironment)");
//        } else {
//
//
//            Configuration myconf = new Configuration();
//            myconf.setInteger(RestOptions.PORT,8081);
//
//            this.configuration= myconf;
//        }
//    }
//
//    public JobExecutionResult execute(String jobName) throws Exception {
//        if (this.executor == null) {
//            this.startNewSession();
//        }
//
//        Plan p = this.createProgramPlan(jobName);
//        JobExecutionResult result = this.executor.executePlan(p);
//        this.lastJobExecutionResult = result;
//        return result;
//    }
//
//    public String getExecutionPlan() throws Exception {
//        Plan p = this.createProgramPlan((String)null, false);
//        if (this.executor != null) {
//            return this.executor.getOptimizerPlanAsJSON(p);
//        } else {
//            PlanExecutor tempExecutor = PlanExecutor.createLocalExecutor(this.configuration);
//            return tempExecutor.getOptimizerPlanAsJSON(p);
//        }
//    }
//
//    @PublicEvolving
//    public void startNewSession() throws Exception {
//        if (this.executor != null) {
//            this.executor.stop();
//            this.jobID = JobID.generate();
//        }
//
//        this.executor = PlanExecutor.createLocalExecutor(this.configuration);
//        this.executor.setPrintStatusDuringExecution(this.getConfig().isSysoutLoggingEnabled());
//        if (this.getSessionTimeout() > 0L) {
//            this.executor.start();
//            this.executorReaper = new LocalEnvironment.ExecutorReaper(this.executor);
//        }
//
//    }
//
//    public String toString() {
//        return "Local Environment (parallelism = " + (this.getParallelism() == -1 ? "default" : this.getParallelism()) + ") : " + this.getIdString();
//    }
//
//    private static class ExecutorReaper {
//        private final LocalEnvironment.ShutdownThread shutdownThread;
//
//        ExecutorReaper(PlanExecutor executor) {
//            this.shutdownThread = new LocalEnvironment.ShutdownThread(executor);
//            this.shutdownThread.start();
//        }
//
//        protected void finalize() throws Throwable {
//            super.finalize();
//            this.shutdownThread.trigger();
//        }
//    }
//
//    private static class ShutdownThread extends Thread {
//        private final Object monitor = new Object();
//        private final PlanExecutor executor;
//        private volatile boolean triggered = false;
//
//        ShutdownThread(PlanExecutor executor) {
//            super("Local cluster reaper");
//            this.setDaemon(true);
//            this.setPriority(1);
//            this.executor = executor;
//        }
//
//        public void run() {
//            synchronized(this.monitor) {
//                while(!this.triggered) {
//                    try {
//                        this.monitor.wait();
//                    } catch (InterruptedException var5) {
//                    }
//                }
//            }
//
//            try {
//                this.executor.stop();
//            } catch (Throwable var4) {
//                System.err.println("Cluster reaper caught exception during shutdown");
//                var4.printStackTrace();
//            }
//
//        }
//
//        void trigger() {
//            this.triggered = true;
//            synchronized(this.monitor) {
//                this.monitor.notifyAll();
//            }
//        }
//    }
//}
