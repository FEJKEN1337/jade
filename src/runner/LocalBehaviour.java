package runner;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class LocalBehaviour extends SimpleBehaviour
{
    private RunnerAgent myAgent;
    private boolean isDone = false;

    public LocalBehaviour(RunnerAgent runner)
    {
        super();
        this.myAgent = runner;
    }

    @Override
    public void action()
    {
        ACLMessage msg = myAgent.blockingReceive();

        if(msg != null)
        {
            if(msg.getPerformative() == ACLMessage.INFORM)
            {
                ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
                reply.addReceiver(msg.getSender());
                myAgent.send(reply);

                System.out.println(myAgent.getLocalName() + " przejmuje pałeczkę z pozycji " + myAgent.getPosition());
                myAgent.addBehaviour(new RunBehaviour(myAgent));
                isDone = true;
                //myAgent.removeBehaviour(this);
            }
            else
            {
                System.out.println(myAgent.getLocalName() + " bug 2");
            }
        }
    }

    @Override
    public boolean done()
    {
        // TODO Auto-generated method stub
        return isDone;
    }

    @Override
    public int onEnd()
    {
        System.out.println(myAgent.getLocalName() + " kończy behaviour Local");
        return super.onEnd();
    }
}
