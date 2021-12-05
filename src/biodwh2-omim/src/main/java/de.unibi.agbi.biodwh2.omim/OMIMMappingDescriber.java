package de.unibi.agbi.biodwh2.omim;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class OMIMMappingDescriber extends MappingDescriber {

    public OMIMMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(Graph graph, Node node, String localMappingLabel) {
        if (OMIMGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (OMIMGraphExporter.PHENOTYP_LABEL.equals(localMappingLabel))
            return describePhenotype(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        String[] names = {"gene_name","included_titles","alternative_title(s)","preferred_title"};
        for(String name: names){
            String n = tryParseString(node.get(name));
            if(n != null){
                description.addName(name);
            }
            else{
                description.addNames(node.<String[]>getProperty(name));
            }
        }
        description.addIdentifier(IdentifierType.HGNC_ID, node.<String>getProperty("approved_gene_symbol"));
        description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty("mim_number"));
        description.addIdentifier(IdentifierType.ENSEMBL_GENE_ID, node.<String>getProperty("ensembl_gene_id"));
        description.addIdentifier(IdentifierType.ENTREZ_GENE_ID, node.<String>getProperty("entrez_gene_id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePhenotype(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PHENOTYPE);
        String[] names = {"phenotype_name","included_titles","alternative_title(s)","preferred_title"};
        for(String name: names){
            String n = tryParseString(node.get(name));
            if(n != null){
                description.addName(name);
            }
            else{description.addNames(node.<String[]>getProperty(name));}
        }
        description.addIdentifier(IdentifierType.OMIM, node.<String>getProperty("mim_number"));
        return new NodeMappingDescription[]{description};
    }

    public String tryParseString(Object value) {
        try {
            return (String)value;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                OMIMGraphExporter.GENE_LABEL, OMIMGraphExporter.PHENOTYP_LABEL};

    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
