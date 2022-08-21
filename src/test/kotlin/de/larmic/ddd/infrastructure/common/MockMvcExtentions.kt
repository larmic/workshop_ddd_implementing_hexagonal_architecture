package de.larmic.ddd.infrastructure.common

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import java.util.*

fun MockMvc.getRoom(id: String) = this.getRoom(UUID.fromString(id))

fun MockMvc.getRoom(id: UUID) = this.perform(
    get("/api/room/$id"))
    .andDo(MockMvcResultHandlers.print())

fun MockMvc.postRoom(json: String) = this.perform(
    post("/api/room")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent()))
    .andDo(MockMvcResultHandlers.print())