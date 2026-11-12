package com.aegisflow.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHashMapLookupTest {

    private InMemoryHashMapLookup<String, Integer> lookup;

    @BeforeEach
    void setUp() {
        lookup = new InMemoryHashMapLookup<>();
    }

    @Test
    void putAndGetStoresAndRetrievesExistingKey() {
        Integer previous = lookup.put("alpha", 10);

        assertThat(previous).isNull();
        assertThat(lookup.get("alpha")).isEqualTo(10);
        assertThat(lookup.size()).isEqualTo(1);
    }

    @Test
    void putOverwritesExistingKeyWithoutIncreasingSize() {
        lookup.put("alpha", 10);
        Integer previous = lookup.put("alpha", 20);

        assertThat(previous).isEqualTo(10);
        assertThat(lookup.get("alpha")).isEqualTo(20);
        assertThat(lookup.size()).isEqualTo(1);
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertThat(lookup.get("missing")).isNull();
    }

    @Test
    void containsKeyReturnsTrueForExistingAndFalseForMissing() {
        lookup.put("alpha", 10);

        assertThat(lookup.containsKey("alpha")).isTrue();
        assertThat(lookup.containsKey("missing")).isFalse();
    }

    @Test
    void removeDeletesExistingEntryAndHandlesMissingKey() {
        lookup.put("alpha", 10);
        lookup.put("beta", 20);

        Integer removed = lookup.remove("alpha");
        Integer missingRemoved = lookup.remove("gamma");

        assertThat(removed).isEqualTo(10);
        assertThat(missingRemoved).isNull();
        assertThat(lookup.containsKey("alpha")).isFalse();
        assertThat(lookup.get("alpha")).isNull();
        assertThat(lookup.size()).isEqualTo(1);
    }

    @Test
    void clearEmptiesAllEntries() {
        lookup.put("alpha", 10);
        lookup.put("beta", 20);

        lookup.clear();

        assertThat(lookup.size()).isZero();
        assertThat(lookup.containsKey("alpha")).isFalse();
        assertThat(lookup.containsKey("beta")).isFalse();
        assertThat(lookup.get("alpha")).isNull();
    }

    @Test
    void separateInstancesMaintainIndependentState() {
        InMemoryHashMapLookup<String, Integer> secondLookup = new InMemoryHashMapLookup<>();
        lookup.put("alpha", 10);

        assertThat(secondLookup.size()).isZero();
        assertThat(secondLookup.containsKey("alpha")).isFalse();
    }
}

