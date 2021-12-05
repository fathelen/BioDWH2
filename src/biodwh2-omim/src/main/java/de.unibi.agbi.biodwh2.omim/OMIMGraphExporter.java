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
        Pattern pheno_name_klammer = Pattern.compile("\\{(.*)\\}");
        Pattern pheno_name_ohne = Pattern.compile("^(.*), \\d{6}");
        Pattern mim_number_pattern = Pattern.compile("(\\d{6})");
        Pattern mapping_key_pattern = Pattern.compile("(\\(\\d\\))");
        Pattern inheritance_pattern = Pattern.compile("\\(\\d\\), (\\D*)");
        List<String> mims = new ArrayList<>();
        ArrayList<Object> list = new ArrayList<>();


        for (final GeneMap2 entry : parseTsvFile(workspace, GeneMap2.class, "genemap2.txt")) {
            list = map.get(entry.MIMNumber);

            if (map.containsKey(entry.MIMNumber)) {
                gene_node = g.addNodeFromModel(entry, "included_titles", list.get(2), "alternative_title(s)", list.get(0), "preferred_title", list.get(1));
                g.update(gene_node);
            } else {
                gene_node = g.addNodeFromModel(entry, "included_titles", null, "alternative_title(s)", null, "preferred_title", null);
                g.update(gene_node);
            }
            if(entry.Phenotypes != null) {
                String[] pheno_split_semikolon = entry.Phenotypes.split(";");

                for (int i = 0; i < pheno_split_semikolon.length; i++) {
                    Matcher matcher_klammer = pheno_name_klammer.matcher(pheno_split_semikolon[i]);
                    Matcher matcher_ohne = pheno_name_ohne.matcher(pheno_split_semikolon[i]);
                    Matcher matcher_mim_number =  mim_number_pattern.matcher(pheno_split_semikolon[i]);
                    Matcher matcher_mapping_key = mapping_key_pattern.matcher(pheno_split_semikolon[i]);
                    Matcher matcher_inheritance = inheritance_pattern.matcher(pheno_split_semikolon[i]);

                    if (matcher_mim_number.find()) {
                        ArrayList<Object> list_pheno = map.get(matcher_mim_number.group(1));
                        if(!mims.contains(matcher_mim_number.group(1))) {
                            pheno_node = g.addNode(PHENOTYP_LABEL, "mim_number", matcher_mim_number.group(1));
                            mims.add(matcher_mim_number.group(1));
                            if(map.containsKey(matcher_mim_number.group(1))){
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
                        }
                        else {
                            g.addEdge(gene_node, g.findNode(PHENOTYP_LABEL,"mim_number",matcher_mim_number.group(1)), "HAS PHENOTYPE");
                            break;
                        }

                    } else {
                        if (!mims.contains(entry.MIMNumber)) {
                            pheno_node = g.addNode(PHENOTYP_LABEL, "mim_number", entry.MIMNumber);
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

                        } else {
                            g.addEdge(gene_node, g.findNode(PHENOTYP_LABEL,"mim_number",entry.MIMNumber), "HAS PHENOTYPE");
                            break;
                        }
                    }
                    g.addEdge(gene_node, pheno_node, "HAS PHENOTYPE");
                    getName(matcher_klammer,matcher_ohne,pheno_node,g,pheno_split_semikolon[i]);
                    getMappingKey(matcher_mapping_key,pheno_node,g);
                    getInheritance(matcher_inheritance,pheno_node,g);
                }

            }
        }
    }

    private void getInheritance(final Matcher matcher, Node node, Graph graph){
        if(matcher.find()){
            node.setProperty("inheritance",matcher.group(1));
            graph.update(node);
        }
        else{
            node.setProperty("inheritance",null);
            graph.update(node);
        }
    }

    private void getMappingKey(final Matcher matcher, Node node, Graph graph){
        if(matcher.find()){
            node.setProperty("mapping_key",matcher.group(1));
            graph.update(node);
        }
        else{
            node.setProperty("mapping_key",null);
            graph.update(node);
        }
    }

    private void getName(final Matcher matcher_mit,Matcher matcher_ohne, Node node, Graph graph, String entry){
        if (entry.contains("{")) {
            if(matcher_mit.find()){
                node.setProperty("phenotype_name", matcher_mit.group(1));
                graph.update(node);
            }
        }
        else{
            if(matcher_ohne.find()) {
                node.setProperty("phenotype_name", matcher_ohne.group(1));
                graph.update(node);
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
