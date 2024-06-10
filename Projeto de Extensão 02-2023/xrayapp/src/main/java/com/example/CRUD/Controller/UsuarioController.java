package com.example.CRUD.Controller;


import com.example.CRUD.Repository.UsuarioRepository;
import com.example.CRUD.Model.Usuario;
import com.example.CRUD.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping(path = "/")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/cadastrar")
    public String formularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastrar";
    }

    @PostMapping ("/cadastrar")
    public String salvarUsuario (@ModelAttribute Usuario usuario){
        usuarioRepository.save(usuario);
        return "index";
    }

    @PostMapping ("/logout")
    public String loguoutUsuario (){
        return "index";
    }

    @GetMapping("/login")
    public String mostrarFormularioDeLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String cpf, @RequestParam String senha, Model model) {
        if (usuarioService.autenticar(cpf, senha)) {
            return "app";
        } else {
            model.addAttribute("error", "CPF ou senha inválidos");
            return "error";
        }
    }

}

