package de.unibi.agbi.biodwh2.omim;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;

import java.util.List;

public class OMIMDataSource extends DataSource {
    public List<GeneMap2> genes;

    @Override
    public String getId() {
        return "OMIM";
    }
    @Override
    public String getFullName() {
        return "Online Mendelian Inheritance in Man";
    }
    @Override
    public String getDescription() {
        return " OMIM is a comprehensive, authoritative compendium of human genes and genetic phenotypes that is freely available and updated daily. The full-text, referenced overviews in OMIM contain information on all known mendelian disorders and over 16,000 genes. OMIM focuses on the relationship between phenotype and genotype. It is updated daily, and the entries contain copious links to other genetics resources.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<OMIMDataSource> getUpdater() {
        return new OMIMUpdater(this);
    }

    @Override
    public Parser<OMIMDataSource> getParser() {
        return new OMIMParser(this);
    }

    @Override
    public GraphExporter<OMIMDataSource> getGraphExporter() {
        return new OMIMGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new OMIMMappingDescriber(this);
    }


    @Override
    protected void unloadData() {

    }

}
