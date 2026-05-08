package com.hse.polochka.feature.family.data.remote

import com.hse.polochka.feature.family.data.dto.FamilyDto
import com.hse.polochka.feature.family.data.dto.InviteFamilyMemberRequestDto
import com.hse.polochka.feature.family.data.dto.InviteFamilyMemberResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FamilyApi {

    @GET("family")
    suspend fun getFamily(
        @Header("Authorization") authorization: String,
    ): Response<FamilyDto>

    @POST("family/invitations")
    suspend fun inviteMember(
        @Header("Authorization") authorization: String,
        @Body request: InviteFamilyMemberRequestDto,
    ): Response<InviteFamilyMemberResponseDto>

    @POST("family/invitations/{inviteToken}/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") authorization: String,
        @Path("inviteToken") inviteToken: String,
    ): Response<FamilyDto>

    @DELETE("family/members/{memberId}")
    suspend fun removeMember(
        @Header("Authorization") authorization: String,
        @Path("memberId") memberId: String,
    ): Response<Unit>
}
