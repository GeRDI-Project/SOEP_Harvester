package harvester;

import de.gerdiproject.harvest.harvester.AbstractHarvester;

import java.security.NoSuchAlgorithmException;

/*
    The main harvester
* */
public class SoepHarvester extends AbstractHarvester {
    private String harvesterName;
    /* As suggested, the constructor should be in a "default" style
        @param harvesterName
    */
    public SoepHarvester(String harvesterName){
        this.harvesterName = harvesterName;
    }

    @Override
    /*
        From the utils-like class, provide access to a collection of SOEP dataset files;
        The overridden implementation should add documents to the search index by calling the addDocument() or
        addDocuments() methods;
    * */
    protected boolean harvestInternal(int i, int i1) throws Exception {
        boolean status = false;

        return false;
    }

    @Override
    protected int initMaxNumberOfDocuments() {
        return 0;
    }

    @Override
    protected String initHash() throws NoSuchAlgorithmException, NullPointerException {
        return null;
    }

    @Override
    protected void abortHarvest() {

    }

    // Demo the app
    public static void main(String[] args){
        // 1. Invoke JGitUtil methods to conduct SOEP dataset retrieval from GitHub

        // 2. Map and index SOEP dataset files retrieved by the previous step
    }
}
