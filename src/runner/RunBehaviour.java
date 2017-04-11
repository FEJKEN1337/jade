package runner;

import common.Utils;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class RunBehaviour extends SimpleBehaviour
{
    private RunnerAgent myAgent;
    private int dest;
    private boolean isDone = false;

    public RunBehaviour(RunnerAgent runner)
    {
        super();
        this.myAgent = runner;
        dest = (myAgent.getPosition() + 1) % Utils.machineCount;
    }

    @Override
    public void action()
    {
        informLocalAgent();

        System.out.println(myAgent.getLocalName() + " TMP1: dest: " + dest);
        ACLMessage msg = myAgent.blockingReceive();
        if(msg != null)
        {
            if(msg.getPerformative() == ACLMessage.CONFIRM)
            {
                move();
                System.out.println(myAgent.getLocalName() + " TMP2: dest: " + dest);
                if(dest == 0)
                {
                    Utils.teamsLaps[myAgent.getTeamId()]++;
                    System.out.println("Druzyna " + myAgent.getTeamId()
                                       + " zrobila okrazenie numer "
                                       + Utils.teamsLaps[myAgent.getTeamId()]);
                    if(Utils.teamsLaps[myAgent.getTeamId()] == Utils.lapsCount)
                    {
                        System.out.println("------------------------------------------------");
                        System.out.println("Czas trwania dla druzyny " +
                                           myAgent.getTeamId() + ": "
                                           + (System.currentTimeMillis() - Utils.startTime) + "ms");
                        System.out.println("------------------------------------------------");
                        myAgent.doDelete();
                        return;
                    }
                }
                myAgent.setPosition(dest);
                System.out.println(myAgent.getLocalName() + " przekazal paleczke");
                myAgent.addBehaviour(new LocalBehaviour(myAgent));
                isDone = true;
                myAgent.removeBehaviour(this);
                System.out.println(myAgent.getLocalName() + " zakonczyl ruch");
            }
            else
            {
                System.out.println(myAgent.getLocalName() + " bug 1");
            }
        }
    }

    private void informLocalAgent()
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        //Dodaje odbiorce o kolejnym ID
        msg.addReceiver(myAgent.getAgents()[(myAgent.getLocalId() + 1) % (Utils.machineCount + 1)]);

        try
        {
            Thread.sleep(Utils.runTime);
        }
        catch(InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        myAgent.send(msg);
    }

    private void move()
    {
        myAgent.doMove(myAgent.getContainerIDs()[dest]);
    }

    @Override
    public boolean done()
    {
        return isDone;
    }

    @Override
    public int onEnd()
    {
        System.out.println(myAgent.getLocalName() + " kończy behaviour Run");
        return super.onEnd();
    }

}
