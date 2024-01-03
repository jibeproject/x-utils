package org.cam;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;

public class CreateGraph {
    public String link;
    public Network network;
    public CreateGraph(){
        this.link = "XX"; // "network/manchester/network.xml"
    }

    public void setLinkToMatsimNetwork(String link){
        this.link = link;
    }

    public void loadMatsimNetwork(){
        // Check if the link is correctly specified

        // Read MATSIM network
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network network = scenario.getNetwork();
        MatsimNetworkReader networkReader = new MatsimNetworkReader(network);
        networkReader.readFile(this.link);
        this.network = network;
    }

    public Network getMatsimNetwork(){
        return this.network;
    }
}
