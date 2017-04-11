package judge;

import jade.core.Agent;

public class JudgeAgent extends Agent
{
    @Override
    protected void setup()
    {
        super.setup();
        addBehaviour(new StartRaceBehaviour());
    }
}
