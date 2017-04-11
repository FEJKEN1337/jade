package runner;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitForStartBehaviour extends OneShotBehaviour
{
    private Behaviour nextBehaviour;

    public WaitForStartBehaviour(Behaviour b)
    {
        this.nextBehaviour = b;
    }

    @Override
    public void action()
    {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
        ACLMessage msg = myAgent.blockingReceive(mt);
        System.out.println(myAgent.getLocalName() + " uzyskłl wiadomość startu");
        myAgent.addBehaviour(nextBehaviour);
    }

}
