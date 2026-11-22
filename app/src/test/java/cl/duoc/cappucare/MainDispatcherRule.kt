package cl.duoc.cappucare

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regla de JUnit para los tests de ViewModels.
 *
 * Los ViewModels lanzan corrutinas en `viewModelScope`, que usa Dispatchers.Main (el hilo de la
 * pantalla). En un test no hay pantalla, así que antes de cada test se reemplaza Main por un
 * dispatcher de prueba, y al terminar se restaura.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
