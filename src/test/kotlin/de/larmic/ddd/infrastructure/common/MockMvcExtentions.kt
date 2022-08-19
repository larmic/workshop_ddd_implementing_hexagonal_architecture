package de.larmic.ddd.infrastructure.common

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

fun MockMvc.getRoom(number: String) = this.perform(
    get("/api/room/$number"))
    .andDo(print())

fun MockMvc.postRoom(json: String) = this.perform(
    post("/api/room")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent()))
    .andDo(print())

fun MockMvc.putPersonToRoom(roomNumber: String, json: String) = this.perform(
    MockMvcRequestBuilders.put("/api/room/$roomNumber/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent()))
    .andDo(print())