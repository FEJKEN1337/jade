package runner;

import common.Utils;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.ContainerID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.List;

import java.util.Iterator;

public class InitContainersBehaviour extends OneShotBehaviour
{
    private Behaviour b;
    private RunnerAgent myAgent;

    public InitContainersBehaviour(RunnerAgent runner, Behaviour b)
    {
        this.b = b;
        this.myAgent = runner;
    }

    @Override
    public void action()
    {
        try
        {
            queryAMS();
            listenForAMSReply();
        }
        catch(CodecException | OntologyException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        move();
        myAgent.addBehaviour(b);
    }

    private void queryAMS() throws CodecException, OntologyException
    {
        QueryPlatformLocationsAction query = new QueryPlatformLocationsAction();
        Action action = new Action(myAgent.getAID(), query);

        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(myAgent.getAMS());
        message.setLanguage(new SLCodec().getName());
        message.setOntology(JADEManagementOntology.getInstance().getName());
        myAgent.getContentManager().fillContent(message, action);
        myAgent.send(message);
    }

    private void listenForAMSReply() throws CodecException,
                                            OntologyException
    {
        ACLMessage receivedMessage = myAgent.blockingReceive(MessageTemplate.MatchSender(myAgent.getAMS()));
        ContentElement content = myAgent.getContentManager().extractContent(receivedMessage);

        // received message is a Result object, whose Value field is a List of
        // ContainerIDs
        Result result = (Result) content;
        List listOfPlatforms = (List) result.getValue();

        // use it
        int i = 0;
        ContainerID[] containers = new ContainerID[Utils.machineCount];
        Iterator iter = listOfPlatforms.iterator();
        while(iter.hasNext())
        {
            ContainerID next = (ContainerID) iter.next();
            //System.out.println(myAgent.getLocalName() + ": " + next.getID());	//tmp
            if(next.getID().startsWith("Main"))
            {
                continue;
            }
            containers[i++] = next;
        }
        myAgent.setContainerIDs(containers);
    }

    private void move()
    {
        if(myAgent.getLocalId() < 0)        //runner
        {
            myAgent.doMove(myAgent.getContainerIDs()[0]);
        }
        else                                //local
        {
            myAgent.doMove(myAgent.getContainerIDs()[myAgent.getPosition()]);
        }
    }

}
