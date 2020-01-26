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

    public static void ExecuteRTIG() throws RTIinternalError {

        if(rti_running){
//         if(isRunning() || rti_running) {
             throw new RTIinternalError("Expected to launch a new RTIG, but one is running already. Kill the process before launch the test.");
        }

        ProcessBuilder processBuilder =  new ProcessBuilder();
        processBuilder.command("rtig");

        try{
            rtig_process = processBuilder.start();
            rti_running = true;
        } catch (Exception e){
            throw new RTIinternalError("Error in RTIG execution");
        }
//
//
//        ProcessBuilder pb = new ProcessBuilder();
//        List<String> commands = new ArrayList<String>();
//        commands.add("pkill rtig");
//        commands.add("rtig");
//        pb.command(commands);
//        try {
//            pb.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
