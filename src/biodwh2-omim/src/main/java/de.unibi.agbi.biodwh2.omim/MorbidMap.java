package de.unibi.agbi.biodwh2.omim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"Phenotype","Gene Symbols", "MIM Number","Cyto Location" })

@GraphNodeLabel("Phenotype")
public class MorbidMap {
    @JsonProperty("Phenotype")
    @GraphProperty("phenotype")
    public String Phenotype;
    @JsonProperty("Gene Symbols")
    @GraphProperty("gene_symbols")
    public String GeneSymbols;
    @JsonProperty("MIM Number")
    @GraphProperty("mim_number")
    public String MIMNumber;
    @JsonProperty("Cyto Location")
    @GraphProperty("cyto_location")
    public String CytoLocation;
}
