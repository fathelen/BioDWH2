package de.unibi.agbi.biodwh2.omim;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OMIMGraphExporter extends GraphExporter<OMIMDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OMIMGraphExporter.class);
    static final String GENE_LABEL = "Gene";
    static final String PHENOTYP_LABEL = "Phenotype";

    public OMIMGraphExporter(final OMIMDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "mim_number", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PHENOTYP_LABEL, "mim_number", IndexDescription.Type.UNIQUE));
        Map<String, ArrayList<Object>> mim_titles_map = null;
        try {
            mim_titles_map = mim_titles(workspace, graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createNodesFromTsvFile(workspace, graph, GeneMap2.class, "genemap2.txt", mim_titles_map);
        return true;
    }

    private void createNodesFromTsvFile(final Workspace workspace, final Graph g, final Class dataType,
                                            final String fileName,final Map<String, ArrayList<Object>> map) throws ExporterException {
        Node gene_node;
        Node pheno_node;
        Pattern python_pattern_long = Pattern.compile("^(.*),\\s(\\d{6})\\s\\((\\d)\\)(|, (.*))$");
        Pattern python_pattern_short = Pattern.compile("^(.*)\\((\\d)\\)(|, (.*))$");
        List<String> mims = new ArrayList<>();
        ArrayList<Object> list = new ArrayList<>();

        for (final GeneMap2 entry : parseTsvFile(workspace, GeneMap2.class, "genemap2.txt")) {
            list = map.get(entry.MIMNumber);

            if (map.containsKey(entry.MIMNumber)) {
                gene_node = g.addNodeFromModel(entry, "included_titles", list.get(2), "alternative_title(s)", list.get(0), "preferred_title", list.get(1));
            } else {
                gene_node = g.addNodeFromModel(entry, "included_titles", null, "alternative_title(s)", null, "preferred_title", null);
            }
            if(entry.Phenotypes != null) {
                String[] pheno_split_semikolon = entry.Phenotypes.split(";");
                for (int i = 0; i < pheno_split_semikolon.length; i++) {
                    pheno_split_semikolon[i] = pheno_split_semikolon[i].trim();
                    Matcher matcher_long = python_pattern_long.matcher(pheno_split_semikolon[i]);
                    Matcher matcher_short = python_pattern_short.matcher(pheno_split_semikolon[i]);
                    if(matcher_long.find()){
                        ArrayList<Object> list_pheno = map.get(matcher_long.group(2));
                        if(!mims.contains(matcher_long.group(2))) {
                            pheno_node = g.addNode(PHENOTYP_LABEL, "phenotype_name", matcher_long.group(1));
                            pheno_node.setProperty("mim_number", matcher_long.group(2));
                            pheno_node.setProperty("mapping_key", matcher_long.group(3));
                            pheno_node.setProperty("inheritance", matcher_long.group(5));
                            mims.add(matcher_long.group(2));
                            if (map.containsKey(matcher_long.group(2))) {
                                pheno_node.setProperty("included_titles", list_pheno.get(2));
                                pheno_node.setProperty("alternative_title(s)", list_pheno.get(0));
                                pheno_node.setProperty("preferred_title", list_pheno.get(1));
                            }
                            else{
                                pheno_node.setProperty("included_titles", null);
                                pheno_node.setProperty("alternative_title(s)", null);
                                pheno_node.setProperty("preferred_title", null);
                            }
                            g.update(pheno_node);
                            g.addEdge(gene_node, pheno_node, "HAS_PHENOTYPE");
                        }
                        else{
                            g.addEdge(gene_node, g.findNode(PHENOTYP_LABEL,"mim_number",matcher_long.group(2)), "HAS_PHENOTYPE");
                            break;
                        }

                    }
                    else {
                        if(matcher_short.find()){
                            if(!mims.contains(entry.MIMNumber)) {
                                pheno_node = g.addNode(PHENOTYP_LABEL,"phenotype_name",matcher_short.group(1));
                                pheno_node.setProperty("mim_number", entry.MIMNumber);
                                pheno_node.setProperty("mapping_key", matcher_short.group(2));
                                pheno_node.setProperty("inheritance",matcher_short.group(3));
                                mims.add(entry.MIMNumber);
                                if(map.containsKey(entry.MIMNumber)){
                                    pheno_node.setProperty("included_titles", list.get(2));
                                    pheno_node.setProperty("alternative_title(s)", list.get(0));
                                    pheno_node.setProperty("preferred_title", list.get(1));
                                }
                                else{
                                    pheno_node.setProperty("included_titles", null);
                                    pheno_node.setProperty("alternative_title(s)", null);
                                    pheno_node.setProperty("preferred_title", null);
                                }
                                g.update(pheno_node);
                                g.addEdge(gene_node, pheno_node, "HAS_PHENOTYPE");
                            }
                            else {
                                g.addEdge(gene_node, g.findNode(PHENOTYP_LABEL,"mim_number",entry.MIMNumber), "HAS_PHENOTYPE");
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, fileName,
                    typeVariableClass);

            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

   private Map<String, ArrayList<Object>> mim_titles(final Workspace workspace,
                                                     final Graph g) throws Exception{
       final Map<String, ArrayList<Object>> mim_titles_map = new HashMap<>();
       for (final MIMTitles title : parseTsvFile(workspace, MIMTitles.class, "mimTitles.txt")) {
           if (!mim_titles_map.containsKey(title.MIMNumber))
               mim_titles_map.put(title.MIMNumber, new ArrayList<>());
           mim_titles_map.get(title.MIMNumber).add(title.AlternativeTitle);
           mim_titles_map.get(title.MIMNumber).add(title.PreferredTitle);
           mim_titles_map.get(title.MIMNumber).add(title.IncludedTitles);
       }
       return mim_titles_map;
   }






}
