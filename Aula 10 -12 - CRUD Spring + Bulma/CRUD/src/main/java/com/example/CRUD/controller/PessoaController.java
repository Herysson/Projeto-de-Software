package com.example.CRUD.controller;

import com.example.CRUD.model.Pessoa;
import com.example.CRUD.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/pessoa")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    //metodo que devolcer um formulário ao usuário
    @GetMapping("/cadastrar")
    public String paginaCadastro(Model model){
        //mandar um objeto do tipo pessoa para a pagina de castro
        model.addAttribute("pessoa",new Pessoa());
        //redireciona para pagina cadastrarPessoa.html
        return "cadastrarPessoa";
    }
    //salva novas pessoas no banco de dados
    @PostMapping(path = "/save")
    public String salvarPessoa(@ModelAttribute Pessoa pessoa, Model model){
        //salvar pessoas no banco;
        pessoaRepository.save(pessoa);
        //Atualizando a lista de pessoa e retornando a página de listar pessoas
        List<Pessoa> listaPessoa = (List<Pessoa>) pessoaRepository.findAll();
        model.addAttribute("listaPessoa", listaPessoa);
        return "listarPessoas";
    }
    //recupera todas as pessoas do banco de dados
    @GetMapping(path = "/listar")
    public String listarPessoas(@ModelAttribute Pessoa pessoa, Model model){
        List<Pessoa> listaPessoa = (List<Pessoa>) pessoaRepository.findAll();
        model.addAttribute("listaPessoa", listaPessoa);
        return "listarPessoas";
    }
    @GetMapping("/alterar/{id}")
    public String alterarPessoa(@PathVariable Long id, Model model){
        Optional<Pessoa> p = pessoaRepository.findById(Math.toIntExact(id));
        model.addAttribute("pessoa", p.get());
        return "alterarPessoa";
    }

    @PostMapping("/alterar")
    public String alterarPessoa(@ModelAttribute Pessoa pessoaAlterada, Model model){
        pessoaRepository.save(pessoaAlterada);
        List <Pessoa> listaPessoa = (List<Pessoa>) pessoaRepository.findAll();

        model.addAttribute("listaPessoa", listaPessoa);
        return "listarPessoas";
    }
}
