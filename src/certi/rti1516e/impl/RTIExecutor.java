package certi.rti1516e.impl;

import hla.rti1516e.exceptions.RTIinternalError;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RTIExecutor {

    private Process rtig_process;

    /*
     * Check if there is a rtig process already launched
     */
    public boolean isRunning() {
        try {
            String process;
            // getRuntime: Returns the runtime object associated with the current Java application.
            // exec: Executes the specified string command in a separate process.
            // When using '|' (pipe) we need to indicate the shell
            String[] cmd = {"/bin/bash","-c", "ps -ax | grep rtig | grep -v grep"};
            //FIXME : grep rtig shouldn't be sentisive to case
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

    /**
     * Check if CERTI_HOST is loaclhost or not
     */
    public static boolean checkLocalHost() {
        // Request to launch a local RTI.
        // Check for a compatible CERTI_HOST environment variable.
        String certiHost = System.getenv("CERTI_HOST");
        if (certiHost != null
                && !certiHost.equals("localhost")
                && !certiHost.equals("127.0.0.1"))
        {
            System.out.println("WARNING : The environment variable CERTI_HOST, which has value: "
            + certiHost + ", is neither localhost nor 127.0.0.1. We cannot launch an RTI at that address. ");
//            throw new Exception("Error in RTIG execution. The environment variable CERTI_HOST, which has value: "
//            + certiHost + ", is neither localhost nor 127.0.0.1. We cannot launch an RTI at that address. ");
            return false;
        }
        return  true;
    }

    public void executeRTIG() throws RTIinternalError {
        System.out.println("Function execute RTIG");


        if(!checkLocalHost()){
            return; //If host=remote, we don't try lo lauch an RTIG
        }

        if(isRunning()) {
             System.out.println("WARNING : Expected to launch a new RTIG, but one is running already. We will use this one. To launch a new RTIG with the test, kill the process before launch the test.");
        } else {
            ProcessBuilder processBuilder =  new ProcessBuilder();
            processBuilder.command("rtig");
            try{
                rtig_process = processBuilder.start();
            } catch (Exception e){
                throw new RTIinternalError("Error in RTIG execution");
            }
        }
    }

    public  void killRTIG() {
        if(rtig_process != null ){
            rtig_process.destroy();
        } else {
            System.out.println("The rtig cannot be killed by this federate since it did not created it.");
        }
    }
}
