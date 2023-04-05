package fr.univnantes.multicore.distanciel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Based off Jakob Jenkov's 'Java Thread Pool Implementation' from https://jenkov.com/tutorials/java-concurrency/thread-pools.html#how-a-thread-pool-works
 */

public class PriorityThreadPool {

    private BlockingQueue<Runnable> highPriorityTaskQueue = null;
    private BlockingQueue<Runnable> lowPriorityTaskQueue = null;
    private List<WorkerRunnable> workers = new ArrayList<>();
    private boolean isStopped = false;

    public PriorityThreadPool(int numberOfThreads){
        //Creates the two queues holding high and low priority queues
        highPriorityTaskQueue = new LinkedBlockingQueue<>();
        lowPriorityTaskQueue = new LinkedBlockingQueue<>();

        //Creates every worker of the thread pool
        for(int i = 0; i < numberOfThreads; i++){
            workers.add(new WorkerRunnable(this));
        }

        //Starts every worker so that they can begin picking tasks and running them
        for(WorkerRunnable workerRunnable: workers){
            new Thread(workerRunnable).start();
        }
    }

    public synchronized void execute(Runnable task, boolean isHighPriority){
        if(this.isStopped) {
            System.out.println("Execute was called on a stopped PriorityThreadPool");
            throw new IllegalStateException("ThreadPool is stopped");
        }


        var targetQueue = isHighPriority? this.highPriorityTaskQueue : this.lowPriorityTaskQueue;

        targetQueue.offer(task);
    }

    public synchronized void stop(){
        this.isStopped = true;
        for(WorkerRunnable workerRunnable : workers){
            workerRunnable.doStop();
        }
    }

    public synchronized void waitUntilAllTasksFinished() {
        while(this.highPriorityTaskQueue.size() + this.lowPriorityTaskQueue.size() > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public BlockingQueue<Runnable> getHighPriorityQueue() {
        return this.highPriorityTaskQueue;
    }

    public BlockingQueue<Runnable> getLowPriorityQueue() {
        return this.lowPriorityTaskQueue;
    }
}


class WorkerRunnable implements Runnable {

    private BlockingQueue<Runnable> highPriorityTaskQueue = null;
    private BlockingQueue<Runnable> lowPriorityTaskqueue = null;
    private Thread thread = null;
    private boolean isStopped = false;
    private boolean returnValue;

    public WorkerRunnable(PriorityThreadPool priorityThreadPool){
        this.highPriorityTaskQueue = priorityThreadPool.getHighPriorityQueue();
        this.lowPriorityTaskqueue = priorityThreadPool.getLowPriorityQueue();
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while(!isStopped){
            try {
                Runnable task;
                if(!this.highPriorityTaskQueue.isEmpty()){
                    task = (Runnable) this.highPriorityTaskQueue.take();
                    task.run();
                }else if (!this.lowPriorityTaskqueue.isEmpty()){
                    task = (Runnable) this.lowPriorityTaskqueue.take();
                    task.run();
                }
            }catch (Exception e){

            }
        }
    }

    public void doStop() {
        isStopped = true;
        this.thread.interrupt();
    }

    public boolean isStopped(){
        return this.isStopped;
    }
}
