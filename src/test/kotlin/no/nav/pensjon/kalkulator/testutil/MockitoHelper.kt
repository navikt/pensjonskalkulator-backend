package no.nav.pensjon.kalkulator.testutil

import org.mockito.Mockito

inline fun <reified T> anyNonNull(): T = Mockito.any(T::class.java)
