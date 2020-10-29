package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        // my code begin
        commumicateLock = new Lock();
        speakCondition = new Condition(commumicateLock);
        listenCondition = new Condition(commumicateLock);
        matchCondition = new Condition(commumicateLock);
        int wordIn = 0;
        boolean waitingForListenFlag = false;
        // my code end
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        // my code begin
        commumicateLock.acquire();
        while(waitingForListenFlag)
        {
            speakCondition.sleep();
        }
        wordIn = word;
        waitingForListenFlag = true;
        listenCondition.wake();
        matchCondition.sleep();
        commumicateLock.release();
        // my code end
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
        // my code begin
        commumicateLock.acquire();
        while(!waitingForListenFlag)
        {
            listenCondition.sleep();
        }
        speakCondition.wake();
        matchCondition.wake();
        waitingForListenFlag = false;
        commumicateLock.release();
        return wordIn;
        // my code end
    }

    // my code begin
    private Lock commumicateLock;
    private Condition speakCondition;
    private Condition listenCondition;
    private Condition matchCondition;
    private int wordIn;
    private boolean waitingForListenFlag;
    // my code end

    // my test code begin
    public static void testCommunicator()
    {
        final Communicator comm = new Communicator();

        KThread speakThread1 = new KThread(
            new Runnable() {
                public void run() {
                    System.out.println("*** speakThread1 in testOfCommunicator run!");
                    comm.speak(100);
                }
            }
        );
        speakThread1.fork();

        KThread speakThread2 = new KThread(
            new Runnable() {
                public void run() {
                    System.out.println("*** speakThread2 in testOfCommunicator run!");
                    comm.speak(200);
                }
            }
        );
        speakThread2.fork();

        final int wordGet[] = new int[2];
        KThread listenThread1 = new KThread(
            new Runnable() {
                public void run() {
                    System.out.println("*** listenThread1 in testOfCommunicator run!");
                    wordGet[0] = comm.listen();
                }
            }
        );
        listenThread1.fork();

        final int wordGet2;
        KThread listenThread2 = new KThread(
            new Runnable() {
                public void run() {
                    System.out.println("*** listenThread2 in testOfCommunicator run!");
                    wordGet[1] = comm.listen();
                }
            }
        );
        listenThread2.fork();

        speakThread1.join();
        speakThread2.join();
        listenThread1.join();
        listenThread2.join();

        Lib.assertTrue((wordGet[0]==100), " Communicate 100 .");
        Lib.assertTrue((wordGet[1]==200), " Communicate 200 .");
    }

    public static void selfTest() {
        testCommunicator();
    }
    // my test code end
}
