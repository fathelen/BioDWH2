package de.unibi.agbi.biodwh2.hgnc;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hgnc.etl.*;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.util.List;

public class HGNCDataSource extends DataSource {
    public List<Gene> genes;

    @Override
    public String getId() {
        return "HGNC";
    }

    @Override
    public Updater getUpdater() {
        return new HGNCUpdater();
    }

    @Override
    public Parser getParser() {
        return new HGNCParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new HGNCRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new HGNCGraphExporter();
    }
}
