package com.example.app.domain.usecase

import com.example.app.domain.model.User
import com.example.app.domain.repository.UserRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Тесты на UseCase (domain-слой).
 * Мокаем интерфейс репозитория: domain-слой не знает про API и реализацию данных.
 */
class GetUserUseCaseTest {

    private val repository = mock<UserRepository>()
    private val useCase = GetUserUseCase(repository)

    private val user = User(id = 1, name = "Ivan", email = "i@test.com")

    @Test
    fun `invoke returns user from repository`() = runTest {
        whenever(repository.getUser(1)).thenReturn(user)

        val result = useCase(1)

        assertEquals(user, result)
    }

    @Test
    fun `invoke passes id to repository`() = runTest {
        whenever(repository.getUser(any())).thenReturn(user)

        useCase(99)

        verify(repository).getUser(99)
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        whenever(repository.getUser(1)).thenThrow(RuntimeException("db error"))

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { useCase(1) }
        }
        assertEquals("db error", exception.message)
    }

    @Test
    fun `invoke calls repository only once`() = runTest {
        whenever(repository.getUser(any())).thenReturn(user)

        useCase(1)

        verify(repository, times(1)).getUser(any())
    }

    @Test
    fun `invoke with invalid id throws`() = runTest {
        whenever(repository.getUser(-1)).thenThrow(IllegalArgumentException("invalid id"))

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(-1) }
        }
        assertEquals("invalid id", exception.message)
    }
}
