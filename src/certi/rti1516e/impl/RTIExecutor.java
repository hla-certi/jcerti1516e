package certi.rti1516e.impl;

import hla.rti1516e.exceptions.RTIinternalError;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RTIExecutor {
    private static RTIExecutor RTI_EXECUTOR = new RTIExecutor();
    private static boolean rti_running;
    private static Process rtig_process;

    public RTIExecutor(){
        rti_running = false;
    }

    /*
     * Check if there is a rtig process already launched
     */

    public static boolean isRunning() {
        try {
            String process;
            // getRuntime: Returns the runtime object associated with the current Java application.
            // exec: Executes the specified string command in a separate process.
            // When using '|' (pipe) we need to indicate the shell
            String[] cmd = {"/bin/sh","-c", "ps -ax | grep rtig | grep -v grep"} ;
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            if ((process = input.readLine()) != null) {
                    System.out.println(process);
                    input.close();
                    return true;
            }
            input.close();
    } catch (Exception err) {
            err.printStackTrace();

    }
        return false;
    }
    
    public static void checkHost() throws Exception {
        // Request to launch a local RTI.
        // Check for a compatible CERTI_HOST environment variable.
        String certiHost = System.getenv("CERTI_HOST");
        if (certiHost != null
                && !certiHost.equals("localhost")
                && !certiHost.equals("127.0.0.1"))
        {
            throw new Exception("Error in RTIG execution. The environment variable CERTI_HOST, which has value: "
            + certiHost + ", is neither localhost nor 127.0.0.1. We cannot launch an RTI at that address. ");
        }
    }

    public static void ExecuteRTIG() throws RTIinternalError {

        try {
            checkHost();
        } catch (Exception e){
            e.printStackTrace();
        }

        if(rti_running || isRunning()) {
             System.out.println("Expected to launch a new RTIG, but one is running already. We will use this one. To launch a new RTIG with the test, kill the process before launch the test.");
        } else {
            ProcessBuilder processBuilder =  new ProcessBuilder();
            processBuilder.command("rtig");

            try{
                rtig_process = processBuilder.start();
                rti_running = true;
            } catch (Exception e){
                throw new RTIinternalError("Error in RTIG execution");
            }
        }
    }

    public static void killRTIG() throws RTIinternalError {
        if(rtig_process != null || rti_running ){
            rtig_process.destroy();
        } else {
            throw new RTIinternalError("Error while killing RTIG, processus doesn't exist");
        }
    }

    public static RTIExecutor getInstance(){
        return RTI_EXECUTOR;
    }

}
