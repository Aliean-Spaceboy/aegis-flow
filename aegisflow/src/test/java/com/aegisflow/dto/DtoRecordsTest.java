package com.aegisflow.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DtoRecordsTest {

    private static final Set<String> STANDARD_RECORD_METHODS = Set.of("equals", "hashCode", "toString");

    static Stream<Class<?>> dtoRecordClasses() {
        return Stream.of(
                TransferRequest.class,
                LedgerReceipt.class,
                LoanApplicationDto.class
        );
    }

    @ParameterizedTest
    @MethodSource("dtoRecordClasses")
    void dtoShouldBePublicFinalEmptyRecord(Class<?> dtoClass) throws Exception {
        assertThat(dtoClass.isRecord()).isTrue();
        assertThat(Modifier.isPublic(dtoClass.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(dtoClass.getModifiers())).isTrue();
        assertThat(dtoClass.getRecordComponents()).isEmpty();
        assertThat(dtoClass.getDeclaredFields()).isEmpty();
        assertThat(dtoClass.getDeclaredAnnotations()).isEmpty();

        assertThat(dtoClass.getDeclaredConstructors()).hasSize(1);
        assertThat(dtoClass.getDeclaredConstructors()[0].getParameterCount()).isZero();

        Set<String> declaredMethodNames = Arrays.stream(dtoClass.getDeclaredMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());
        assertThat(declaredMethodNames).containsExactlyInAnyOrderElementsOf(STANDARD_RECORD_METHODS);

        Object instance = dtoClass.getDeclaredConstructor().newInstance();
        assertThat(instance).isNotNull();
    }
}

