package lol.apex.util.math;

public class TimerUtil {
    private long lastMs = System.currentTimeMillis(); 

    public boolean delay(float value) {
        return System.currentTimeMillis() - lastMs > value;
    } 


    public boolean delay(double ms) {
        return System.currentTimeMillis() - lastMs > ms;
    }

    public float getElapsedTime() {
        return System.currentTimeMillis() - lastMs;
    } 

    public void reset() {
        lastMs = System.currentTimeMillis();
    } 

    public boolean passed(double ms, boolean resetOnPass) {
        if (delay(ms)) {
            if (resetOnPass) reset();
            return true;
        }
        return false;
    }
}