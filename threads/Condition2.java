package nachos.threads;

import nachos.machine.*;
import java.util.ArrayList;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;
        // my-code-begin
        this.waitQueue = ThreadedKernel.scheduler.newThreadQueue(false);
        // my-code-end
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	    Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    
        // my-code-begin
        boolean status = Machine.interrupt().disable();
        conditionLock.release();
        waitQueue.waitForAccess(KThread.currentThread());
        KThread.currentThread().sleep();
        conditionLock.acquire();
        Machine.interrupt().restore(status);
        // my-code-end
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        // my-code-begin
        boolean status = Machine.interrupt().disable();
        KThread thread = waitQueue.nextThread();
        if (!(thread==null)){
            thread.ready();
        }
        Machine.interrupt().restore(status);
        // my-code-end
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    
        // my-code-begin
        boolean status = Machine.interrupt().disable();
        KThread thread = waitQueue.nextThread();
        while (!(thread==null)){
            thread.ready();
            thread = waitQueue.nextThread();
        }
        Machine.interrupt().restore(status);
        // my-code-end
    }

    private Lock conditionLock;
    // my-code-begin
    private ThreadQueue waitQueue;
    // my-code-end

    // my test code begin
    private static class TestCondition2
    {
        private static Lock tLock;
        private static Condition tCondition;

        private static Lock tLock2;
        private static Condition2 tCondition2;

        private ArrayList<String> seqThreadName;
        private ArrayList<String> seqThreadName2;

        public TestCondition2(){
            tLock = new Lock();
            tCondition = new Condition(tLock);

            tLock2 = new Lock();
            tCondition2 = new Condition2(tLock2);

            System.out.println("\n Tests Of Condition2");
            test1();
            test2();
        }

        // 测Condition2的sleep+wake
        private void test1(){
            seqThreadName2 = new ArrayList<String>();

            KThread thread1 = new KThread(
                new Runnable() {
                    public void run() {
                        System.out.println("*** thread1 in Condition2 test1 run!");
                        tLock2.acquire();
                        for (int i=0; i<3; i++) {
                            String currentThreadName = KThread.currentThread().getName();
                            System.out.println("1 current thread is: "+currentThreadName);
                            seqThreadName2.add(currentThreadName);
                            tCondition2.wake();
                            tCondition2.sleep();
                        }
                        tLock2.release();
                    }
                }
            );
            thread1.setName("thread1");
            thread1.fork();

            KThread thread2 = new KThread(
                new Runnable() {
                    public void run() {
                        System.out.println("*** thread2 in Condition2 test1 run!");
                        tLock2.acquire();
                        for (int i=0; i<3; i++) {
                            String currentThreadName = KThread.currentThread().getName();
                            System.out.println("2 current thread is: "+currentThreadName);
                            seqThreadName2.add(currentThreadName);
                            tCondition2.wake();
                            tCondition2.sleep();
                        }
                        tLock2.release();
                    }
                }
            );
            thread2.setName("thread2");
            thread2.fork();

            thread2.join();
        }

        // 测Condition2的sleep+wakeall
        private void test2(){
            KThread thread1 = new KThread(
                new Runnable() {
                    public void run() {
                        System.out.println("*** thread1 in Condition2 test2 run!");
                        tLock2.acquire();
                        for (int i=0; i<5; i++) {
                            String currentThreadName = KThread.currentThread().getName();
                            System.out.println("3 current thread is: "+currentThreadName);
                            tCondition2.sleep();
                        }
                        tLock2.release();
                    }
                }
            );
            thread1.setName("thread1 in Condition2 test2");
            thread1.fork();

            KThread thread2 = new KThread(
                new Runnable() {
                    public void run() {
                        System.out.println("*** thread2 in Condition2 test2 run!");
                        tLock2.acquire();
                        for (int i=0; i<5; i++) {
                            String currentThreadName = KThread.currentThread().getName();
                            System.out.println("4 current thread is: "+currentThreadName);
                            tCondition2.sleep();
                            tCondition2.wake();
                        }
                        tLock2.release();
                    }
                }
            );
            thread2.setName("thread2 in Condition2 test2");
            thread2.fork();

            KThread thread3 = new KThread(
                new Runnable() {
                    public void run() {
                        System.out.println("*** thread3 in Condition2 test3 run!");
                        tLock2.acquire();
                        for (int i=0; i<5; i++) {
                            String currentThreadName = KThread.currentThread().getName();
                            System.out.println("5 current thread is: "+currentThreadName);
                            tCondition2.wakeAll();
                            tCondition2.sleep();
                        }
                        tLock2.release();
                    }
                }
            );
            thread3.setName("thread3 in Condition2 test2");
            thread3.fork();

            thread3.join();
            thread1.join();
            thread2.join();
        }

    }

    // 和其它的自测放在一起
    public static void selfTest() {
        new TestCondition2();
    }

    // my test code end
}
