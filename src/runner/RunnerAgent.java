package runner;

import common.Utils;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.mobility.MobilityOntology;

public class RunnerAgent extends Agent
{
    ContainerID[] containerIDs = new ContainerID[Utils.machineCount];
    AID[] agents = new AID[Utils.machineCount + 1];
    int localId = -1;
    int teamId = -1;
    int position = -1;

    @Override
    protected void setup()
    {
        // TODO Auto-generated method stub
        super.setup();
        init();

        Object[] args = getArguments();

        localId = (int) args[0];
        teamId = (int) args[1];
        position = localId % Utils.machineCount;

        for(int i = 0; i <= Utils.machineCount; ++i)
        {
            agents[i] = new AID("local" + teamId + "#" + i, AID.ISLOCALNAME);
        }

        if(localId > 0)
        {
            addBehaviour(new InitContainersBehaviour(
                this,
                new WaitForStartBehaviour(new LocalBehaviour(this))
            ));
        }
        else
        {    //run with current machine number
            addBehaviour(new InitContainersBehaviour(
                this,
                new WaitForStartBehaviour(new RunBehaviour(this))
            ));
        }
    }

    @Override
    protected void afterMove()
    {
        init();
    }

    void init()
    {
        // Register language and ontology
        getContentManager().registerLanguage(new SLCodec());
        getContentManager().registerOntology(JADEManagementOntology.getInstance());
        getContentManager().registerOntology(MobilityOntology.getInstance());
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public AID[] getAgents()
    {
        return agents;
    }

    public int getTeamId()
    {
        return teamId;
    }

    public ContainerID[] getContainerIDs()
    {
        return containerIDs;
    }

    public void setContainerIDs(ContainerID[] containerIDs)
    {
        this.containerIDs = containerIDs;
    }

    public int getLocalId()
    {
        return localId;
    }

    public void setLocalId(int localId)
    {
        this.localId = localId;
    }
}
