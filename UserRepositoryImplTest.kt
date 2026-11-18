package com.example.app.data.repository

import com.example.app.data.api.ApiService
import com.example.app.data.dto.UserDto
import com.example.app.data.mapper.UserMapper
import com.example.app.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Тесты на реализацию репозитория (data-слой).
 * API-сервис и маппер подменяются моками: репозиторий тестируем изолированно.
 */
class UserRepositoryImplTest {

    private val api = mock<ApiService>()
    private val mapper = mock<UserMapper>()
    private val repository = UserRepositoryImpl(api, mapper)

    private val dto = UserDto(id = 1, name = "Ivan", email = "i@test.com")
    private val domain = User(id = 1, name = "Ivan", email = "i@test.com")

    @Test
    fun `getUser returns domain model`() = runTest {
        whenever(api.getUser(1)).thenReturn(dto)
        whenever(mapper.fromDto(dto)).thenReturn(domain)

        val result = repository.getUser(1)

        assertEquals(domain, result)
    }

    @Test
    fun `getUser calls api with correct id`() = runTest {
        whenever(api.getUser(any())).thenReturn(dto)
        whenever(mapper.fromDto(any())).thenReturn(domain)

        repository.getUser(42)

        verify(api).getUser(42)
    }

    @Test
    fun `getUser throws exception when api fails`() = runTest {
        whenever(api.getUser(1)).thenThrow(RuntimeException("network error"))

        val exception = assertThrows(RuntimeException::class.java) {
            runTest { repository.getUser(1) }
        }
        assertEquals("network error", exception.message)
    }

    @Test
    fun `getUsers returns empty list for empty response`() = runTest {
        whenever(api.getUsers()).thenReturn(emptyList())

        val result = repository.getUsers()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getUsers maps every element`() = runTest {
        whenever(api.getUsers()).thenReturn(listOf(dto, dto))
        whenever(mapper.fromDto(any())).thenReturn(domain)

        val result = repository.getUsers()

        assertEquals(2, result.size)
        verify(mapper, times(2)).fromDto(any())
    }
}
