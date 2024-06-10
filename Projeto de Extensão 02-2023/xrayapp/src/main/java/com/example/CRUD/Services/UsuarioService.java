package com.example.CRUD.Services;

import com.example.CRUD.Model.Usuario;
import com.example.CRUD.Repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean autenticar(String cpf, String senha) {
        return usuarioRepository.findByCpf(cpf)
                .map(usuario -> usuario.getSenha().equals(senha))
                .orElse(false);
    }
}
