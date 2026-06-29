package com.ibm.auth.service;

import com.ibm.auth.common.payload.ApiResponse;
import com.ibm.auth.payload.request.LoginRequest;
import com.ibm.auth.payload.request.SignupRequest;
import com.ibm.auth.payload.response.LoginResponse;

public interface AuthService {

    ApiResponse<Void> signup(SignupRequest request);

    ApiResponse<LoginResponse> login(LoginRequest request);

}