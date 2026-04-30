package br.univates.dtos;

public record AuthenticationResponseDTO(String token, String name, String email, Long id)
{

}