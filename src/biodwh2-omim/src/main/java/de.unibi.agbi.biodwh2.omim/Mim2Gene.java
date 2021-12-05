package de.unibi.agbi.biodwh2.omim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "MIM Number", "MIM Entry Type", "Entrez Gene ID (NCBI)", "Approved Gene Symbol (HGNC)","Ensembl Gene ID (Ensembl)"

})
@GraphNodeLabel("Gene2")
public class Mim2Gene {
    @JsonProperty("MIM Number")
    @GraphProperty("mim_number")
    public String MIMNumber;
    @JsonProperty("MIM Entry Type")
    @GraphProperty("mim_entry_type")
    public String MIMEntryType;
    @JsonProperty("Entrez Gene ID (NCBI)")
    @GraphProperty("entrez_gene_id")
    public String NCBI;
    @JsonProperty("Approved Gene Symbol (HGNC)")
    @GraphProperty("approved_gene_symbol")
    public String HGNC;
    @JsonProperty("Ensembl Gene ID (Ensembl)")
    @GraphProperty("ensembl_gene_id")
    public String Ensembl;

}
