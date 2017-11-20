package harvester;

import de.gerdiproject.harvest.IDocument;
import de.gerdiproject.harvest.harvester.AbstractHarvester;
import de.gerdiproject.harvest.harvester.AbstractListHarvester;
import harvester.json.SoepDomain;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

/*
    The main harvester
* */
public class SoepHarvester extends AbstractListHarvester<SoepDomain> {
    private String harvesterName;
    // As suggested, the constructor should be in a "default" style
    public SoepHarvester(){
        super(1);
        this.harvesterName = "SOEP Harvester";
    }

    @Override
    protected Collection<SoepDomain> loadEntries() {

        return null;
    }

    @Override
    protected List<IDocument> harvestEntry(SoepDomain soepDomain) {
        return null;
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
