import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;


public class ProcessName {
    public static void main(String[] args) {
        // 获取进程快照
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        Tlhelp32.PROCESSENTRY32 processEntry = new Tlhelp32.PROCESSENTRY32();

        // 遍历所有进程
        try {
            if (Kernel32.INSTANCE.Process32First(snapshot, processEntry)) {
                do {
                    // 打印进程 ID 和名称
                    System.out.printf("进程名称: %s | 进程 ID: %d%n",
                            Native.toString(processEntry.szExeFile),
                            processEntry.th32ProcessID.intValue());
                } while (Kernel32.INSTANCE.Process32Next(snapshot, processEntry));
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(snapshot); // 释放句柄
        }
    }
}
