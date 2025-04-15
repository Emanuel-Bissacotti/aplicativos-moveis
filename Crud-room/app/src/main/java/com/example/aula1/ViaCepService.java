package com.example.aula1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCepService {
    @GET("{cep}/json/")
    Call<Endereco> getEndereco(@Path("cep") String cep);
}