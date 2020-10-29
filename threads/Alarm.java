package nachos.threads;

import nachos.machine.*;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p>
     * <b>Note</b>: Nachos will not function correctly with more than one alarm.
     */
    public Alarm() {

        // my code begin
        waitingThreadMap = new HashMap<KThread, Long>();
        // my code end

        Machine.timer().setInterruptHandler(new Runnable() {
            public void run() {
                timerInterrupt();
            }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current thread
     * to yield, forcing a context switch if there is another thread that should be
     * run.
     */
    public void timerInterrupt() {
        long currTime = Machine.timer().getTime();

        // my code begin
        Iterator mapIterator = waitingThreadMap.entrySet().iterator();
        HashMap.Entry<KThread, Long> mapItem;
        KThread thread;
        long wakeTime;
        while(mapIterator.hasNext())
        {
            mapItem = (HashMap.Entry<KThread, Long>) mapIterator.next();
            thread = mapItem.getKey();
            wakeTime = waitingThreadMap.get(thread);
            if (wakeTime <= currTime)
            {
                thread.ready();
                waitingThreadMap.remove(thread);
            }

        }

        // my code end
        KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks, waking it up in
     * the timer interrupt handler. The thread must be woken up (placed in the
     * scheduler ready set) during the first timer interrupt where
     *
     * <p>
     * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
     *
     * @param x the minimum number of clock ticks to wait.
     *
     * @see nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {

        // my code begin
        if  (x <= 0) return;
        // my code end

        long wakeTime = Machine.timer().getTime() + x;
        
        // my code begin
        boolean intStatus = Machine.interrupt().disable();
        waitingThreadMap.put(KThread.currentThread(), wakeTime);
        KThread.sleep();
        Machine.interrupt().restore(intStatus);
        // my code end
    }

    // my code begin
    private HashMap<KThread, Long> waitingThreadMap;
    // my code end

    
    // my test code begin
    public static void selfTest() {
        System.out.println("test Of Alarm run");
        long timeStart = Machine.timer().getTime();
        ThreadedKernel.alarm.waitUntil(10);
        long timeEnd = Machine.timer().getTime();
        Lib.assertTrue((timeEnd-timeStart>=10), " Alarm test 10.");
    }
    // my test code end

}
