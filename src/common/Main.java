package common;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import judge.JudgeAgent;
import runner.RunnerAgent;

import java.io.IOException;

public class Main
{
    private static jade.core.Runtime rt = jade.core.Runtime.instance();
    private static AgentContainer container;

    public static void main(String[] args) throws StaleProxyException
    {
        Utils.teamsLaps = new int[Utils.teamCount];

        if(Utils.isMain)
        {
            rt.setCloseVM(true);
        }

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, Utils.ip);
        profile.setParameter(Profile.MAIN_PORT, Utils.port);

        if(Utils.isMain)
        {
            container = rt.createMainContainer(profile);
            AgentController rma;
            try
            {
                rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
                rma.start();
            }
            catch(ControllerException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            container = rt.createAgentContainer(profile);
        }
        //SimulateMachines();
        try
        {
            Thread.sleep(Utils.startDelay);    //could be changed to read()
            Profile profile1 = new ProfileImpl();
            rt.createAgentContainer(profile1);
            System.out.println("Press enter when machines are ready");
            System.in.read();
        }
        catch(IOException | InterruptedException e)
        {
            // TODO Auto-generated catch blockk
            e.printStackTrace();
        }
        startTeams(Utils.teamDisplacement, Utils.teamsRunHere);
        //Total number of teams on all machines must sum up to Utils.machineCount
        if(Utils.isMain)
        {
            AgentController acJudge = container.createNewAgent("judge", JudgeAgent.class.getName(), null);

            try
            {
                System.out.println("Press enter when to start race");
                System.in.read();
            }
            catch(IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            acJudge.start();
        }
        rt.shutDown();
    }

    private static void startTeams(int k, int n) throws StaleProxyException
    {
        for(int j = k; j < k + n; ++j)
        {
            for(int i = 0; i <= Utils.machineCount; ++i)
            {
                Object[] tabLocal = {i, j};
                AgentController acLocal = container.createNewAgent("local" + j + "#" + i, RunnerAgent.class.getName(), tabLocal);
                acLocal.start();
            }
        }
    }
}
