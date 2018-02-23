package com.k0bl.nppestomysql.processor;


import com.k0bl.nppestomysql.model.Taxonomy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TaxonomyItemProcessor implements ItemProcessor<Taxonomy, Taxonomy> {

    @Override
    public Taxonomy process(Taxonomy Taxonomy) throws Exception {
        final String code = Taxonomy.getCode();
        final String grouping = Taxonomy.getGrouping();
        final String classification = Taxonomy.getClassification();
        final String specialization = Taxonomy.getSpecialization();
        final Taxonomy transformedTaxonomy = new Taxonomy(code, grouping, classification, specialization);
        log.info("Converting (" + Taxonomy + ") into (" + transformedTaxonomy + ")");

        return transformedTaxonomy;
    }
}