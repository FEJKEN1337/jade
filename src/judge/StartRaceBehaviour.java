package judge;

import common.Utils;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class StartRaceBehaviour extends OneShotBehaviour
{
    AMSAgentDescription[] agents = null;

    @Override
    public void action()
    {
        System.out.println("Sędzia rozpoczyna wyścig");
        SearchConstraints c = new SearchConstraints();
        c.setMaxResults(-1L);
        try
        {
            agents = AMSService.search(myAgent, new AMSAgentDescription(), c);

            ArrayList<AMSAgentDescription> filtered = new ArrayList<AMSAgentDescription>();

            for(AMSAgentDescription agent : agents)
            {
                if(agent.getName().getLocalName().startsWith("local"))
                {
                    System.out.println("Agent dodany: " + agent.getName().getLocalName());
                    filtered.add(agent);
                }
            }

            System.out.println("Agenci, ktorzy zostana wystartowani: ");
            for(int i = 0; i < filtered.size(); i++)
            {
                AID agentID = filtered.get(i).getName();
                System.out.println(agentID.getLocalName());
            }
            System.out.println();

            sendMessages(filtered);
        }
        catch(FIPAException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendMessages(List<AMSAgentDescription> agents)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
        for(AMSAgentDescription agent : agents)
        {
            msg.addReceiver(agent.getName());
        }
        //Czy moze wysylac wiadomosc bez tresci
        initiateTimes();
        myAgent.send(msg);
        System.out.println("Sędzia wysłał start");
    }

    private void initiateTimes()
    {
        Utils.times = new long[Utils.teamCount];
        Utils.startTime = System.currentTimeMillis();
    }

}
