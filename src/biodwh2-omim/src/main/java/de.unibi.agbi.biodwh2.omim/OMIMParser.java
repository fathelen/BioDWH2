package de.unibi.agbi.biodwh2.omim;

import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import java.util.List;

public class OMIMParser extends SingleFileCsvParser<OMIMDataSource,GeneMap2>{


    public OMIMParser(OMIMDataSource dataSource){
        super(dataSource,GeneMap2.class,true,CsvType.TSV,OMIMUpdater.FILE_GENEMAP2);
    }


    @Override
    protected void storeResults(OMIMDataSource dataSource, List<GeneMap2> results) {
        dataSource.genes =results;
    }
}
