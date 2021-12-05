package de.unibi.agbi.biodwh2.omim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;


@JsonPropertyOrder({"Chromosom","Genomic Position Start","Genomic Position End", "Cyto Location",
        "Computed Cyto Location","MIM Number","Gene Symbols", "Gene Name","Approved Gene Symbol",
        "Entrez Gene ID","Ensembl Gene ID","Comments","Phenotypes","Mouse Gene Symbol/ID" })

@GraphNodeLabel("Gene")
public class GeneMap2 {
    @JsonProperty("Chromosom")
    @GraphProperty("chromosom")
    public String Chromosom;
    @JsonProperty("Genomic Position Start")
    @GraphProperty("genomic_position_start")
    public String GenomicPS;
    @JsonProperty("Genomic Position End")
    @GraphProperty("genomic_position_end")
    public String GenomicPE;
    @JsonProperty("Cyto Location")
    @GraphProperty("cyto_location")
    public String CytoLocation;
    @JsonProperty("Computed Cyto Location")
    @GraphProperty("computed_cyto_location")
    public String ComputedCytoLocation;
    @JsonProperty("MIM Number")
    @GraphProperty("mim_number")
    public String MIMNumber;
    @JsonProperty("Gene Symbols")
    @GraphProperty("gene_symbols")
    public String GeneSymbols;
    @JsonProperty("Gene Name")
    @GraphProperty("gene_name")
    public String GeneName;
    @JsonProperty("Approved Gene Symbol")
    @GraphProperty("approved_gene_symbol")
    public String ApprovedGeneSymbol;
    @JsonProperty("Entrez Gene ID")
    @GraphProperty("entrez_gene_id")
    public String EntrezGeneID;
    @JsonProperty("Ensembl Gene ID")
    @GraphProperty("ensembl_gene_id")
    public String EnsemblGeneID;
    @JsonProperty("Comments")
    @GraphProperty("comments")
    public String Comments;
    @JsonProperty("Phenotypes")
   // @GraphArrayProperty(value = "phenotypes", arrayDelimiter = ",")
    public String Phenotypes;
    @JsonProperty("Mouse Gene Symbol/ID")
    @GraphProperty("mouse_gene_symbol/id")
    public String MouseGene;

}


