package de.unibi.agbi.biodwh2.omim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"Prefix", "MIM Number", "Preferred Title; symbol","Alternative Title(s); symbol(s)","Included Title(s); symbols" })

//@GraphNodeLabel("Gene")
public class MIMTitles {
    @JsonProperty("Prefix")
    @GraphProperty("prefix")
    public String Prefix;
    @JsonProperty("MIM Number")
    @GraphProperty("mim_number")
    public String MIMNumber;
    @JsonProperty("Preferred Title; symbol")
    @GraphProperty("preferred_title")
    public String PreferredTitle;
    @JsonProperty("Alternative Title(s); symbol(s)")
    //@GraphProperty("alternative_title(s)")
    @GraphArrayProperty(value = "alternative_title(s)", arrayDelimiter = ";;")
    public String AlternativeTitle;
    @JsonProperty("Included Title(s); symbols")
    //@GraphProperty("included_titles")
    @GraphArrayProperty(value = "included_titles", arrayDelimiter = ";;")
    public String IncludedTitles;

}
