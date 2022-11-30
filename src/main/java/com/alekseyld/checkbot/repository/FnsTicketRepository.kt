package com.alekseyld.checkbot.repository

import com.alekseyld.checkbot.model.FnsFindTicketResponse
import com.alekseyld.checkbot.model.FnsTicketResponse
import com.alekseyld.checkbot.model.QrCodeData
import com.alekseyld.checkbot.properties.FnsProperties
import com.google.gson.Gson
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.springframework.stereotype.Repository


@Repository
class FnsTicketRepository(
    val fnsProperties: FnsProperties,
    val gson: Gson
) {

    private val httpClient = HttpClientBuilder.create().build()

    fun findTicketBy(qrCodeData: QrCodeData) : FnsFindTicketResponse {

        val requestData = mapOf("qr" to qrCodeData.source)

        val response = httpClient.execute(
            HttpPost(BASE_URL + FIND_TICKET)
                .apply {
                    setHeader("Content-type", "application/json")
                    entity = StringEntity(gson.toJson(requestData))
                }
                .addAuthTokenHeader()
        )

        val entity = response.entity
        val responseString = EntityUtils.toString(entity, "UTF-8")

        return gson.fromJson(responseString, FnsFindTicketResponse::class.java)
    }

    fun getTicket(ticketId: String): FnsTicketResponse {
        val response = httpClient.execute(
            HttpGet(BASE_URL + GET_TICKET + ticketId)
                .addAuthTokenHeader()
        )

        val entity = response.entity
        val responseString = EntityUtils.toString(entity, "UTF-8")

        return gson.fromJson(responseString, FnsTicketResponse::class.java)
    }

    private fun HttpRequestBase.addAuthTokenHeader() : HttpRequestBase {
        addHeader("sessionId", fnsProperties.sessionId)
        return this
    }

    companion object {
        const val BASE_URL = "https://irkkt-mobile.nalog.ru:8888"
        const val FIND_TICKET = "/v2/ticket"
        const val GET_TICKET = "/v2/tickets/"
    }
}