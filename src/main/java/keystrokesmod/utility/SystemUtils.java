package keystrokesmod.utility;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class SystemUtils {
    
    public static long getUsedMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        return heapMemoryUsage.getUsed() / (1024 * 1024);
    }
    
    public static long getMaxMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        return heapMemoryUsage.getMax() / (1024 * 1024);
    }
    
    public static int getUsedMemoryPercent() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        return (int) (heapMemoryUsage.getUsed() * 100 / heapMemoryUsage.getMax());
    }
    
    public static String getOSName() {
        return System.getProperty("os.name");
    }
    
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }
}
