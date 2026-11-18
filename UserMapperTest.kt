package com.example.app.data.mapper

import com.example.app.data.dto.UserDto
import com.example.app.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Тесты на маппер DTO <-> Domain.
 * Проверяем корректность полей, симметрию преобразований и обработку null/пустых значений.
 */
class UserMapperTest {

    private val mapper = UserMapper()

    @Test
    fun `fromDto maps all fields correctly`() {
        val dto = UserDto(id = 1, name = "Ivan", email = "ivan@test.com")

        val result = mapper.fromDto(dto)

        assertEquals(1, result.id)
        assertEquals("Ivan", result.name)
        assertEquals("ivan@test.com", result.email)
    }

    @Test
    fun `fromDto handles empty name`() {
        val dto = UserDto(id = 2, name = "", email = "a@b.c")

        val result = mapper.fromDto(dto)

        assertEquals("", result.name)
    }

    @Test
    fun `toDto maps all fields correctly`() {
        val domain = User(id = 3, name = "Maria", email = "m@test.com")

        val result = mapper.toDto(domain)

        assertEquals(3, result.id)
        assertEquals("Maria", result.name)
        assertEquals("m@test.com", result.email)
    }

    @Test
    fun `fromDto and toDto are symmetrical`() {
        val dto = UserDto(id = 5, name = "Alex", email = "x@test.com")

        val roundTrip = mapper.toDto(mapper.fromDto(dto))

        assertEquals(dto, roundTrip)
    }

    @Test
    fun `fromDto handles nullable field`() {
        val dto = UserDto(id = 7, name = null, email = "e@e.com")

        val result = mapper.fromDto(dto)

        assertEquals(null, result.name)
    }
}
