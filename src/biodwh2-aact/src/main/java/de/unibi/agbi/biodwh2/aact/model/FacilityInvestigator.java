package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "facility_id", "role", "name"})
public class FacilityInvestigator {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("facility_id")
    public Long facilityId;
    @JsonProperty("role")
    public String role;
    @JsonProperty("name")
    public String name;
}
