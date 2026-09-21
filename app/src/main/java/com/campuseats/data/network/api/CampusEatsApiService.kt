package com.campuseats.data.network.api

import com.campuseats.data.network.model.ApiResponse
import com.campuseats.data.network.model.AuthResponseDto
import com.campuseats.data.network.model.CreateOrderRequestDto
import com.campuseats.data.network.model.LoginRequestDto
import com.campuseats.data.network.model.OrderResponseDto
import com.campuseats.data.network.model.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface blueprint for the remote Campus Eats backend.
 * Provides the clean foundation for future remote network synchronization.
 */
interface CampusEatsApiService {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    @POST("api/v1/orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequestDto
    ): Response<ApiResponse<OrderResponseDto>>

    @GET("api/v1/orders/student/{studentId}")
    suspend fun getStudentOrders(
        @Path("studentId") studentId: String
    ): Response<ApiResponse<List<OrderResponseDto>>>

    @GET("api/v1/orders/vendor/{vendorId}")
    suspend fun getVendorOrders(
        @Path("vendorId") vendorId: String
    ): Response<ApiResponse<List<OrderResponseDto>>>
}
