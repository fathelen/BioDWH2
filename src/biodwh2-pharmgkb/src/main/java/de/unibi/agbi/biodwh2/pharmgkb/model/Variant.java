package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Variant {
    @Parsed(field = "Variant ID")
    public String variantId;
    @Parsed(field = "Variant Name")
    public String variantName;
    @Parsed(field = "Gene IDs")
    public String geneIds;
    @Parsed(field = "Gene Symbols")
    public String geneSymbols;
    @Parsed(field = "Location")
    public String location;
    @Parsed(field = "Variant Annotation count")
    public String variantAnnotationCount;
    @Parsed(field = "Clinical Annotation count")
    public String clinicalAnnotationCount;
    @Parsed(field = "Level 1/2 Clinical Annotation count")
    public String level12ClinicalAnnotationCount;
    @Parsed(field = "Guideline Annotation count")
    public String guidelineAnnotationCount;
    @Parsed(field = "Label Annotation count")
    public String labelAnnotationCount;
    @Parsed(field = "Synonyms")
    public String synonyms;
}
