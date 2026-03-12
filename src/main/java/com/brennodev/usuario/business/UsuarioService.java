package com.brennodev.usuario.business;

import com.brennodev.usuario.business.converter.UsuarioConverter;
import com.brennodev.usuario.business.dto.UsuarioDTO;
import com.brennodev.usuario.infrastructure.entity.Usuario;
import com.brennodev.usuario.infrastructure.exceptions.ConflictException;
import com.brennodev.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.brennodev.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;


    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);

        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }


    public void emailExiste(String email){
        try {
            boolean existe =  verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email já cadastrado" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email já cadastrado", e.getCause());
        }
    }
    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado" + email));
    }

    public void detelaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }
}
