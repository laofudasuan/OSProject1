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
        Condition speakCondition = new Condition(commumicateLock);
        Condition listenCondition = new Condition(commumicateLock);
        Condition matchCondition = new Condition(commumicateLock);
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
}
